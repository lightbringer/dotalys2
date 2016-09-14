package de.lighti.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.lighti.SkadiDotaPlay;
import skadistats.clarity.model.Entity;

public class SkadiEntity implements de.lighti.model.Entity {

    private final skadistats.clarity.model.Entity internalEntity;
    private final Map<String, Object> properties;

    public SkadiEntity( Entity entity ) {
        internalEntity = entity;
        properties = new HashMap();
    }

    @Override
    public EntityClass getEntityClass() {
        return SkadiEntityClass.forDTClass( internalEntity.getDtClass() );
    }

    @Override
    public int getId() {
        return internalEntity.getIndex();
    }

    @Override
    public String getName() {
        final Integer index = getProperty( NAME_INDEX );
        return SkadiDotaPlay.entityNames.get( index );
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap( properties );
    }

    @Override
    public <T> T getProperty( String string ) {
        return (T) properties.get( string );
    }

    public void setProperty( String name, Object o ) {
        properties.put( name, o );
    }

    @Override
    public String toString() {
        return internalEntity.toString();
    }
}
