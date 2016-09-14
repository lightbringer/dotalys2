package de.lighti.io.data;

import java.util.Iterator;
import java.util.NavigableSet;

import org.hibernate.collection.internal.PersistentSortedSet;
import org.hibernate.engine.spi.SessionImplementor;

@SuppressWarnings( "unchecked" )
public class PersistentNavigationableSet extends PersistentSortedSet implements NavigableSet {

    public PersistentNavigationableSet( SessionImplementor session ) {
        super( session );
    }

    public PersistentNavigationableSet( SessionImplementor session, NavigableSet colllection ) {
        super( session, colllection );
    }

    @Override
    public Object ceiling( Object e ) {
        return ((NavigableSet) set).ceiling( e );
    }

    @Override
    public Iterator descendingIterator() {
        return ((NavigableSet) set).descendingIterator();
    }

    @Override
    public NavigableSet descendingSet() {
        return ((NavigableSet) set).descendingSet();
    }

    @Override
    public Object floor( Object e ) {
        return ((NavigableSet) set).floor( e );
    }

    @Override
    public NavigableSet headSet( Object toElement, boolean inclusive ) {
        return ((NavigableSet) set).headSet( toElement, inclusive );
    }

    @Override
    public Object higher( Object e ) {
        return ((NavigableSet) set).higher( e );
    }

    @Override
    public Object lower( Object e ) {
        return ((NavigableSet) set).lower( e );
    }

    @Override
    public Object pollFirst() {
        return ((NavigableSet) set).pollFirst();
    }

    @Override
    public Object pollLast() {
        return ((NavigableSet) set).pollLast();
    }

    @Override
    public NavigableSet subSet( Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive ) {
        return ((NavigableSet) set).subSet( fromElement, fromInclusive, toElement, toInclusive );
    }

    @Override
    public NavigableSet tailSet( Object fromElement, boolean inclusive ) {
        return ((NavigableSet) set).tailSet( fromElement, inclusive );
    }

}
