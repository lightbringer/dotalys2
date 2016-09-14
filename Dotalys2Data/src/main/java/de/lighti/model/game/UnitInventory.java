package de.lighti.model.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Access( AccessType.PROPERTY )
class UnitInventory implements Cloneable {

    private long id;

    private List<Dota2Item> items = Arrays.asList( new Dota2Item[Hero.BAG_SIZE] );

    @Override
    protected Object clone() {

        final UnitInventory clone = new UnitInventory();
        clone.items = new ArrayList<Dota2Item>( items );

        return clone;

    }

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE )
    private long getId() {
        return id;
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem0() {
        return items.get( 0 );
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem1() {
        return items.get( 1 );
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem2() {
        return items.get( 2 );
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem3() {
        return items.get( 3 );
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem4() {
        return items.get( 4 );
    }

    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    @ManyToOne
    private Dota2Item getItem5() {
        return items.get( 5 );
    }

    @Transient
    public List<Dota2Item> getItems() {
        return items;
    }

    private void setId( long id ) {
        this.id = id;
    }

    private Dota2Item setItem0( Dota2Item i ) {
        return items.set( 0, i );
    }

    private Dota2Item setItem1( Dota2Item i ) {
        return items.set( 1, i );
    }

    private Dota2Item setItem2( Dota2Item i ) {
        return items.set( 2, i );
    }

    private Dota2Item setItem3( Dota2Item i ) {
        return items.set( 3, i );
    }

    private Dota2Item setItem4( Dota2Item i ) {
        return items.set( 4, i );
    }

    private Dota2Item setItem5( Dota2Item i ) {
        return items.set( 5, i );
    }

    public void setItems( List<Dota2Item> items ) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "UnitInventory [items=" + items + "]";
    }

}