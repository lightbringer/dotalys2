package de.lighti.components.map;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;

import de.lighti.components.map.data.XYZDataSet;
import de.lighti.components.map.data.XYZSeries;
import de.lighti.model.Statics;

/**
 * The MapCanvasComponent renders different variations of the Dota2 minimap
 * along with a series of markers. The component has a fixed size of 512x512.
 *
 * @author TobiasMahlmann
 *
 */
public class MapCanvasComponent extends ChartPanel {
    /**
     *
     */
    private static final long serialVersionUID = 2077175805479363567L;
    private static final int DEFAULT_DOT_SIZE = 3;
    private BufferedImage minimap;
    private BufferedImage minimapModel;
    private String MINIMAP_FILE;

    private final String MINIMAP_MODEL_FILE = "resources/Mapmodel.png";
    private boolean paintMapModel;

    /**
     * Default constructor.
     */
    public MapCanvasComponent( ) {
        super( new JFreeChart( new XYPlot( new XYZDataSet(), new NumberAxis( "X" ), new NumberAxis( "Y" ), new CustomXYDotRenderer() ) ) );

        final Dimension size = new Dimension( 512, 512 );
        setMinimumSize( size );
        setMaximumSize( size );
        setPreferredSize( size );
        setPopupMenu( null );

        final XYPlot plot = (XYPlot) getChart().getPlot();
        ((CustomXYDotRenderer) plot.getRenderer()).setDotHeight( DEFAULT_DOT_SIZE );
        ((CustomXYDotRenderer) plot.getRenderer()).setDotWidth( DEFAULT_DOT_SIZE );
//        resetDotSize();
        plot.getRangeAxis().setVisible( false );
        plot.getDomainAxis().setVisible( false );
        getChart().getLegend().setVisible( false );
        plot.setDomainGridlinesVisible( false );
        plot.setRangeGridlinesVisible( false );
        plot.setDomainPannable( false );
        plot.setRangePannable( false );
        plot.setBackgroundImageAlpha( 1f );
        ((NumberAxis) plot.getDomainAxis()).setRange( 64, 64 + 128 );
        ((NumberAxis) plot.getRangeAxis()).setRange( 64, 64 + 128 );

        paintMapModel = false;

        MINIMAP_FILE = "resources/Minimap.jpg";

        setGameVersion( Statics.SUPPORTED_PROTOCOL_VERSION );
    }

    public void addSeries( XYZSeries series ) {
        final XYZDataSet s = (XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset();
        s.addSeries( series );
    }

    public void endUpdate() {
        ((XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset()).setPropagateEvents( true );

    }

    /**
     * @return the pencil size for painting the markers
     */
    public int getDotSize() {
        return ((XYDotRenderer) ((XYPlot) getChart().getPlot()).getRenderer()).getDotHeight();
    }

    public int getItemCount() {
        final XYZDataSet s = (XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset();
        if (s.getSeriesCount() == 0) {
            return 0;
        }
        else {
            return s.getSeries( 0 ).getItemCount();
        }
    }

    public long getTimeStampFromIndex( int series, int index ) {
        final XYZDataSet sc = (XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset();

        if (index == 0) {
            return (long) sc.getZ( series, index );
        }
        else {
            return (long) sc.getZ( series, index - 1 );
        }

    }

    public boolean hasSeries( XYZSeries series ) {
        final XYZDataSet s = (XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset();
        return s.getSeries( series.getKey() ) != null;
    }

    /**
     * @return true if the zone map is currently displayed
     */
    public boolean isPaintMapModel() {
        return paintMapModel;
    }

    public void removeSeries( XYZSeries xyz ) {
        final XYZDataSet s = (XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset();
        s.removeSeries( xyz );
    }

    private void selectMinimap() {
        try {

            URL url = MapCanvasComponent.class.getResource( MINIMAP_FILE );
            minimap = ImageIO.read( url );
            getChart().getPlot().setBackgroundImage( minimap );

            url = MapCanvasComponent.class.getResource( MINIMAP_MODEL_FILE );
            minimapModel = ImageIO.read( url );
        }
        catch (final IOException e) {
            JOptionPane.showMessageDialog( this, e.getLocalizedMessage(), "Error loading minimap", JOptionPane.ERROR_MESSAGE );
        }
    }

    public void setGameVersion( int version ) {
        final String s = version > 40 ? "resources/Minimap_6.82.png" : "resources/Minimap.jpg";
        if (!s.equals( MINIMAP_FILE )) {
            MINIMAP_FILE = s;
            selectMinimap();
            repaint();
        }
    }

    /**
     * Set this to true if the component should render the annotated zone map
     * instead of the schematic representation of the game map.
     * @param paintMapModel
     */
    public void setPaintMapModel( boolean paintMapModel ) {
        this.paintMapModel = paintMapModel;
        if (paintMapModel) {
            if (MINIMAP_FILE == "resources/Minimap_6.82.png") {
                JOptionPane.showMessageDialog( this, "We haven't updated the zones for 6.8.2 and newer games" );
            }
            getChart().getPlot().setBackgroundImage( minimapModel );
        }
        else {
            getChart().getPlot().setBackgroundImage( minimap );
        }
    }

    public void startUpdate() {
        ((XYZDataSet) ((XYPlot) getChart().getPlot()).getDataset()).setPropagateEvents( false );

    }

}
