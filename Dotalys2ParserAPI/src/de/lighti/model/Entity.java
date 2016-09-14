package de.lighti.model;

import java.util.Map;

public interface Entity {
    String NAME_INDEX = "m_pEntity.m_nameStringableIndex";
    String CELL_X = "CBodyComponent.m_cellX";
    String CELL_Y = "CBodyComponent.m_cellY";

    public static int getIndexForReference( int reference ) {
        return reference & (1 << 14) - 1;
    }

    EntityClass getEntityClass();

    int getId();

    String getName();

    Map<String, Object> getProperties();

    <T> T getProperty( String string );
}
