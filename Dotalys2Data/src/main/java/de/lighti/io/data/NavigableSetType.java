package de.lighti.io.data;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

public class NavigableSetType implements UserCollectionType {

    @Override
    public boolean contains( Object collection, Object o ) {
        final NavigableSet m = (NavigableSet) collection;
        return m.contains( o );
    }

    @Override
    public Iterator getElementsIterator( Object o ) {
        final NavigableSet m = (NavigableSet) o;
        return m.iterator();
    }

    @Override
    public Object indexOf( Object arg0, Object arg1 ) {
        return null;
    }

    @Override
    public Object instantiate( int anticipatedSize ) {
        return new TreeSet();
    }

    @Override
    public PersistentCollection instantiate( SessionImplementor session, CollectionPersister persister ) throws HibernateException {
        return new PersistentNavigationableSet( session );
    }

    @Override
    public Object replaceElements( Object original, Object target, CollectionPersister persister, Object owner, Map copyCache,
                    SessionImplementor sessionssionImplementor ) throws HibernateException {
        final NavigableSet setA = (NavigableSet) original;
        final NavigableSet setB = (NavigableSet) target;
        setB.clear();
        setB.addAll( setA );

        return target;
    }

    @Override
    public PersistentCollection wrap( SessionImplementor session, Object colllection ) {
        return new PersistentNavigationableSet( session, (NavigableSet) colllection );
    }

}
