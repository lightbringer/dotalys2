package de.lighti.components.map.data;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jfree.data.general.Series;

public class XYZSeries extends Series {
    public class XYZDataItem {
        public int x = -1;
        public int y = -1;
        public long z = -1;
        public Image image = null;
    }

    private TreeMap<Long, XYZDataItem> items;
    private List<XYZDataItem> order;

    private Image defaultImage;

    private boolean useImages;

    private Color color;

    private boolean visible;

    public XYZSeries( Comparable<?> key ) {
        super( key );

        setNotify( true );
        order = new ArrayList<XYZDataItem>();
        items = new TreeMap<Long, XYZDataItem>();
        visible = true;
    }

    public XYZDataItem add( int x, int y, long z ) {

        final XYZDataItem d = new XYZDataItem();
        d.x = x;
        d.y = y;
        d.z = z;
        items.put( z, d );
        order.add( d );
        if (getNotify()) {
            fireSeriesChanged();
        }
        return d;
    }

    public void addAll( XYZSeries src ) {
        items.putAll( src.items );
        order.addAll( src.items.values() );

        if (getNotify()) {
            fireSeriesChanged();
        }
    }

    public void addOrReplace( XYZDataItem i ) {

        final XYZDataItem existing = items.put( i.z, i );
        if (existing != null) {
            order.set( order.indexOf( existing ), i );
        }
        else {
            order.add( i );
        }

    }

    public XYZDataItem findItem( long z ) {
        final Entry<Long, XYZDataItem> e = items.floorEntry( z );
        return e != null ? e.getValue() : null;
    }

    public XYZDataItem getByZ( long item ) {
        return items.get( item );
    }

    public Image getImage( int item ) {
        final Image i = getItem( item ).image;
        return i != null ? i : defaultImage;
    }

    public XYZDataItem getItem( int item ) {
        return order.get( item );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public TreeMap<Long, XYZDataItem> getItemsUnsafe() {
        return items;
    }

    public Color getSeriesColor() {
        return color;
    }

    public Number getX( int item ) {
        return getItem( item ).x;
    }

    public Number getY( int item ) {
        return getItem( item ).y;
    }

    public Number getZ( int item ) {
        return getItem( item ).z;
    }

    public boolean isUseImages() {
        return useImages;
    }

    public boolean isVisible() {
        return visible;
    }

    public void removeAll() {
        items.clear();
        order.clear();
        if (getNotify()) {
            fireSeriesChanged();
        }
    }

    public void set( XYZSeries src ) {
        items = new TreeMap<Long, XYZDataItem>( src.items );
        order = new ArrayList<XYZDataItem>( src.items.values() );

        if (getNotify()) {
            fireSeriesChanged();
        }

    }

    public void setDefaultImage( Image defaultImage ) {
        this.defaultImage = defaultImage;

        if (getNotify()) {
            fireSeriesChanged();
        }
    }

    public void setImage( int item, Image image ) {
        items.get( item ).image = image;
        if (getNotify()) {
            fireSeriesChanged();
        }
    }

    public void setSeriesColor( Color c ) {
        color = c;

    }

    public void setUseImages( boolean b ) {
        useImages = b;

    }

    public void setVisible( boolean b ) {
        visible = b;

    }

}