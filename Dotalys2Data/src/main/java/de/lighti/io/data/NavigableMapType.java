package de.lighti.io.data;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

public class NavigableMapType implements UserCollectionType {

    @Override
    public boolean contains( Object collection, Object o ) {
        return ((NavigableMap) collection).containsKey( o );
    }

    @Override
    public Iterator getElementsIterator( Object o ) {
        final NavigableMap m = (NavigableMap) o;
        return m.values().iterator();
    }

    @Override
    public Object indexOf( Object arg0, Object arg1 ) {
        return null;
    }

    @Override
    public Object instantiate( int anticipatedSize ) {
        return new TreeMap();
    }

    @Override
    public PersistentCollection instantiate( SessionImplementor session, CollectionPersister persister ) throws HibernateException {
        return new PersistentNavigationableMap( session );
    }

    @Override
    public Object replaceElements( Object original, Object target, CollectionPersister persister, Object owner, Map copyCache,
                    SessionImplementor sessionssionImplementor ) throws HibernateException {
        final NavigableMap setA = (NavigableMap) original;
        final NavigableMap setB = (NavigableMap) target;
        setB.clear();
        setB.putAll( setA );

        return target;
    }

    @Override
    public PersistentCollection wrap( SessionImplementor session, Object colllection ) {
        return new PersistentNavigationableMap( session, (NavigableMap) colllection );
    }

}
