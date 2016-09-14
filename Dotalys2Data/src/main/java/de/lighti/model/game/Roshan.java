package de.lighti.model.game;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public class Roshan extends Hero {

    public Roshan() {
        super( "CDOTA_Unit_Roshan", 0 );

    }

    @Override
    public String toString() {
        return "Roshan [getName()=" + getKey() + "]";
    }

}
