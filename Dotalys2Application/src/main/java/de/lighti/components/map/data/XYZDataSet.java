package de.lighti.components.map.data;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractXYZDataset;

public class XYZDataSet extends AbstractXYZDataset {
    private final List<XYZSeries> data;

    private boolean propagateEvents;

    public XYZDataSet() {
        data = new LinkedList();
        propagateEvents = true;

    }

    public void addSeries( XYZSeries series ) {
        data.add( series );

        series.addChangeListener( this );
        if (propagateEvents) {
            fireDatasetChanged();
        }
    }

    @Override
    public int getItemCount( int series ) {
        return data.get( series ).getItemCount();
    }

    public XYZSeries getSeries( Comparable series ) {
        return data.stream().filter( s -> s.getKey().equals( series ) ).findAny().orElse( null );
    }

    public XYZSeries getSeries( int series ) {
        return data.get( series );
    }

    @Override
    public int getSeriesCount() {
        return data.size();
    }

    @Override
    public Comparable getSeriesKey( int series ) {
        return data.get( series ).getKey();
    }

    @Override
    public Number getX( int series, int item ) {
        return data.get( series ).getX( item );
    }

    @Override
    public Number getY( int series, int item ) {
        return data.get( series ).getY( item );
    }

    @Override
    public Number getZ( int series, int item ) {
        return data.get( series ).getZ( item );
    }

    @Override
    public int indexOf( Comparable key ) {
        return data.indexOf( getSeries( key ) );
    }

    public void removeSeries( XYZSeries xys ) {
        if (data.remove( xys )) {
            xys.removeChangeListener( this );
            if (propagateEvents) {
                fireDatasetChanged();
            }
        }
    }

    @Override
    public void seriesChanged( SeriesChangeEvent event ) {
        if (propagateEvents) {
            fireDatasetChanged();
        }
    }

    public void setPropagateEvents( boolean propagateEvents ) {
        this.propagateEvents = propagateEvents;
        if (propagateEvents) {
            fireDatasetChanged();
        }
    }

}
