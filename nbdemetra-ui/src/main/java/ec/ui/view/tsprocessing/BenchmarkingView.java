/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.nbdemetra.ui.NbComponents;
import ec.tss.Ts;
import ec.tss.TsFactory;
import ec.tss.html.implementation.HtmlTsDifferenceDocument;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.Disposables;
import ec.ui.chart.JTsChart;
import ec.ui.grid.JTsGrid;
import ec.ui.interfaces.IDisposable;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author Jean Palate
 */
public class BenchmarkingView extends JComponent implements IDisposable {

    private final JTsGrid grid_;
    private final JTsChart chart_;
    private final JTsChart dchart_;
    private final Box documentPanel_;
    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();

    public BenchmarkingView() {
        setLayout(new BorderLayout());

        grid_ = new JTsGrid();
        grid_.setTsUpdateMode(TsUpdateMode.None);
        chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        dchart_ = new JTsChart();
        documentPanel_ = Box.createHorizontalBox();

        JSplitPane splitpane1 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, true, chart_, dchart_);
        splitpane1.setDividerLocation(0.7);
        splitpane1.setResizeWeight(0.3);

        JSplitPane splitpane2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, true, grid_, documentPanel_);
        splitpane2.setDividerLocation(0.7);
        splitpane2.setResizeWeight(0.3);

        JSplitPane splitpane3 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitpane1, splitpane2);
        splitpane3.setDividerLocation(0.5);
        splitpane3.setResizeWeight(0.5);


        this.add(splitpane3, BorderLayout.CENTER);
    }

    public void setTsToolkit(ITsViewToolkit toolkit) {
        toolkit_ = toolkit;
    }

    public ITsViewToolkit getTsToolkit() {
        return toolkit_;
    }

    public void set(Ts benchSa, Ts sa, boolean mul) {
        ArrayList<Ts> all = new ArrayList<>();
        all.add(sa);
        all.add(benchSa);
        chart_.getTsCollection().replace(all);
        TsData sdiff = mul ? (TsData.divide(benchSa.getTsData(), sa.getTsData()).minus(1))
                : TsData.subtract(benchSa.getTsData(), sa.getTsData());
        Ts diff = TsFactory.instance.createTs("Differences");
        diff.set(sdiff);
        dchart_.getTsCollection().replace(diff);
        all.add(diff);
        grid_.getTsCollection().replace(all);

        HtmlTsDifferenceDocument document = new HtmlTsDifferenceDocument(benchSa, sa, mul);
        Disposables.disposeAndRemoveAll(documentPanel_).add(toolkit_.getHtmlViewer(document));
        chart_.updateUI();
        dchart_.updateUI();
    }

    @Override
    public void dispose() {
        grid_.dispose();
        chart_.dispose();
        dchart_.dispose();
    }
}
