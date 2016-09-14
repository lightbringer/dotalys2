package de.lighti.model;

import java.util.HashMap;
import java.util.Map;

import skadistats.clarity.model.DTClass;

public class SkadiEntityClass implements EntityClass {
    private static Map<DTClass, SkadiEntityClass> clazzes = new HashMap<DTClass, SkadiEntityClass>();

    static SkadiEntityClass forDTClass( DTClass clazz ) {
        if (!clazzes.containsKey( clazz )) {
            clazzes.put( clazz, new SkadiEntityClass( clazz ) );
        }
        return clazzes.get( clazz );
    }

    private final DTClass clazz;

    public SkadiEntityClass( DTClass clazz ) {
        this.clazz = clazz;

    }

    @Override
    public String getName() {
        return clazz.getDtName();
    }

}
