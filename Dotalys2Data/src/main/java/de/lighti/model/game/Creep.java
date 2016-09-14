package de.lighti.model.game;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access( AccessType.FIELD )
public class Creep extends Unit {

    public Creep( String key, int entityId ) {
        super( key, entityId );
        // TODO Auto-generated constructor stub
    }

}
