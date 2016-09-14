package de.lighti.io.data;

import java.util.NavigableMap;
import java.util.NavigableSet;

import org.hibernate.collection.internal.PersistentSortedMap;
import org.hibernate.engine.spi.SessionImplementor;

@SuppressWarnings( "unchecked" )
public class PersistentNavigationableMap extends PersistentSortedMap implements NavigableMap {

    public PersistentNavigationableMap( SessionImplementor session ) {
        super( session );
    }

    public PersistentNavigationableMap( SessionImplementor session, NavigableMap colllection ) {
        super( session, colllection );
    }

    @Override
    public Entry ceilingEntry( Object key ) {
        return ((NavigableMap) map).ceilingEntry( key );
    }

    @Override
    public Object ceilingKey( Object key ) {
        return ((NavigableMap) map).ceilingKey( key );
    }

    @Override
    public NavigableSet descendingKeySet() {
        return ((NavigableMap) map).descendingKeySet();
    }

    @Override
    public NavigableMap descendingMap() {
        return ((NavigableMap) map).descendingMap();
    }

    @Override
    public Entry firstEntry() {
        return ((NavigableMap) map).firstEntry();
    }

    @Override
    public Entry floorEntry( Object key ) {
        return ((NavigableMap) map).floorEntry( key );
    }

    @Override
    public Object floorKey( Object key ) {
        return ((NavigableMap) map).floorKey( key );
    }

    @Override
    public NavigableMap headMap( Object toKey, boolean inclusive ) {
        return ((NavigableMap) map).headMap( toKey, inclusive );
    }

    @Override
    public Entry higherEntry( Object key ) {
        return ((NavigableMap) map).higherEntry( key );
    }

    @Override
    public Object higherKey( Object key ) {
        return ((NavigableMap) map).higherKey( key );
    }

    @Override
    public Entry lastEntry() {
        return ((NavigableMap) map).lastEntry();
    }

    @Override
    public Entry lowerEntry( Object key ) {
        return ((NavigableMap) map).lowerEntry( key );
    }

    @Override
    public Object lowerKey( Object key ) {
        return ((NavigableMap) map).lowerKey( key );
    }

    @Override
    public NavigableSet navigableKeySet() {
        return ((NavigableMap) map).navigableKeySet();
    }

    @Override
    public Entry pollFirstEntry() {
        return ((NavigableMap) map).pollFirstEntry();
    }

    @Override
    public Entry pollLastEntry() {
        return ((NavigableMap) map).pollLastEntry();
    }

    @Override
    public NavigableMap subMap( Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive ) {
        return ((NavigableMap) map).subMap( fromKey, fromInclusive, toKey, toInclusive );
    }

    @Override
    public NavigableMap tailMap( Object fromKey, boolean inclusive ) {
        return ((NavigableMap) map).tailMap( fromKey, inclusive );
    }

}
