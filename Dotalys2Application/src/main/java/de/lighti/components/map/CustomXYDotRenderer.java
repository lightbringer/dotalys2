package de.lighti.components.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import de.lighti.components.map.data.XYZDataSet;
import de.lighti.components.map.data.XYZSeries;

class CustomXYDotRenderer extends XYDotRenderer {
    private static final Logger LOGGER = Logger.getLogger( CustomXYDotRenderer.class.getName() );
    private final Map<XYZSeries, Paint> paintList;

    public CustomXYDotRenderer() {
        paintList = new HashMap<XYZSeries, Paint>();
    }

    private void drawDot( Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis,
                    ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass ) {
        // get the data point...
        final double x = dataset.getXValue( series, item );
        final double y = dataset.getYValue( series, item );
        final double adjx = (getDotWidth() - 1) / 2.0;
        final double adjy = (getDotHeight() - 1) / 2.0;
        if (!Double.isNaN( y )) {
            final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            final double transX = domainAxis.valueToJava2D( x, dataArea, xAxisLocation ) - adjx;
            final double transY = rangeAxis.valueToJava2D( y, dataArea, yAxisLocation ) - adjy;

            g2.setPaint( getSeriesPaint( (XYZDataSet) dataset, series ) );
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect( (int) transY, (int) transX, getDotHeight(), getDotWidth() );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect( (int) transX, (int) transY, getDotWidth(), getDotHeight() );
            }

            final int domainAxisIndex = plot.getDomainAxisIndex( domainAxis );
            final int rangeAxisIndex = plot.getRangeAxisIndex( rangeAxis );
            updateCrosshairValues( crosshairState, x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation );
        }

    }

    @Override
    public void drawItem( Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis,
                    ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass ) {
        final XYZDataSet xyzData = (XYZDataSet) dataset;
        final XYZSeries dataSeries = xyzData.getSeries( series );
        if (!dataSeries.isVisible()) {
            return;
        }

        if (dataSeries.getImage( item ) == null || !dataSeries.isUseImages()) {
            drawDot( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass );

        }
        else {

            final Image image = dataSeries.getImage( item );
            final Number x = dataSeries.getX( item );
            final Number y = dataSeries.getY( item );
            if (x == null || y == null) {
                LOGGER.warning( "item " + item + " of series " + series + " contains no data. BUG?" );
                return;
            }
            final double adjx = image.getWidth( null ) / 2;
            final double adjy = image.getHeight( null ) / 2;

            final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            final double transX = domainAxis.valueToJava2D( (int) x, dataArea, xAxisLocation ) - adjx;
            final double transY = rangeAxis.valueToJava2D( (int) y, dataArea, yAxisLocation ) - adjy;

            g2.drawImage( image, (int) transX, (int) transY, image.getWidth( null ), image.getHeight( null ), null );

        }

    }

    private Paint getSeriesPaint( XYZDataSet dataset, int seriesIndex ) {
        // return the override, if there is one...
        final XYZSeries series = dataset.getSeries( seriesIndex );

        // otherwise look up the paint list
        Paint seriesPaint = paintList.get( series );
        if (seriesPaint == null) {
            if (series.getSeriesColor() != null) {
                seriesPaint = series.getSeriesColor();
                paintList.put( series, seriesPaint );
            }
            else {
                final DrawingSupplier supplier = getDrawingSupplier();
                if (supplier != null) {
                    seriesPaint = supplier.getNextPaint();
                    paintList.put( series, seriesPaint );
                }
                else {
                    seriesPaint = getBasePaint();
                }
            }
        }

        return seriesPaint;
    }

}