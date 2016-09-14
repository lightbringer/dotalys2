package de.lighti.model.game;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Access( AccessType.FIELD )
public class CombatEvent {
    public enum Type {
        DAMGE_RECEIVED, DAMAGE_DONE, HEAL_RECEIVED, HEAL_DONE, DEATH
    };

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;
    public long tick;

    @ManyToOne
    @Cascade( value = { CascadeType.SAVE_UPDATE } )
    @JoinColumn( name = "SOURCE_ID" )
    public Unit source;

    @ManyToOne
    @Cascade( value = { CascadeType.SAVE_UPDATE } )
    @JoinColumn( name = "TARGET_ID" )
    public Unit target;
    public int value;
    public int x;
    public int y;
    public CombatEvent.Type type;
    public String inflictor;

}