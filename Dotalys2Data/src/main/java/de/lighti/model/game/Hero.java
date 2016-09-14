package de.lighti.model.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.SortNatural;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@Access( AccessType.FIELD )
public class Hero extends Unit {
    @Entity
    @Access( AccessType.FIELD )
    public class ItemEvent {
        @ManyToOne
        @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
        public Dota2Item item;
        public int slot;
        public boolean added;
        public long tick;

        @Id
        @GeneratedValue( strategy = GenerationType.TABLE )
        private long id;

        private ItemEvent( long tick, Dota2Item item, int slot, boolean added ) {
            super();
            this.tick = tick;
            this.item = item;
            this.slot = slot;
            this.added = added;
        }

    }

    public final static int BAG_SIZE = 12; //two bags of 6. Not sure where courier itmes go

    private static int countItem( UnitInventory list, Dota2Item n ) {
        if (n == null) {
            throw new IllegalArgumentException( "n must not be null" );
        }
        int count = 0;
        for (final Dota2Item m : list.getItems()) {
            if (m != null && m.getKey().equals( n.getKey() )) {
                count++;
            }
        }
        return count;
    }

    @SortNatural
    @Cascade( { CascadeType.ALL } )
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @OneToMany
    @MapKeyColumn( name = "time" )
    private final SortedMap<Long, UnitInventory> inventories;
    @OneToMany
    @Cascade( { CascadeType.SAVE_UPDATE, CascadeType.PERSIST } )
    //Abilities are owned by Replay
    //TODO should not happen
    private final Set<Ability> abilities;
    @OneToMany
    @Cascade( { CascadeType.ALL } )
    private final List<ItemEvent> itemLog;
    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    private final SortedMap<Long, Integer> gold;

    @SortNatural
    @CollectionType( type = "de.lighti.io.data.NavigableMapType" )
    @ElementCollection
    private final SortedMap<Long, Integer> xp;

    public Hero( String name, int entityId ) {
        super( name, entityId );

        gold = new TreeMap<Long, Integer>();
        gold.put( 0l, 0 );
        xp = new TreeMap<Long, Integer>();
        xp.put( 0l, 0 );
        inventories = new TreeMap<Long, UnitInventory>();
        inventories.put( 0l, new UnitInventory() );
        itemLog = new ArrayList<ItemEvent>();

        abilities = new HashSet<Ability>();

    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public Ability getAbilityByName( String name ) {
        for (final Ability a : abilities) {
            if (a.getKey().equals( name )) {
                return a;
            }
        }
        return null;
    }

    /**
     * Get all items held by this hero. Beware that this will give
     * you a Dota2Item for every internal Entity that was attached to this hero, the set
     * might return a number of objects for the same actual game item. If you're interested
     * in the unique items a hero bought, you should use getItemLog() instead.
     * @return a set of items
     */
    public Set<Dota2Item> getAllItems() {
        final Set<Dota2Item> ret = new HashSet<Dota2Item>();
        for (final UnitInventory a : inventories.values()) {
            ret.addAll( a.getItems() );
        }
        return ret;
    }

    public double getCombatRange() {
        return HeroRange.valueOf( getKey() ).combatRange;
    }

    public int getGold( long time ) {
        return ((NavigableMap<Long, Integer>) gold).floorEntry( time ).getValue();
    }

    public double getHealRange() {
        return HeroRange.valueOf( getKey() ).healRange;
    }

    public List<ItemEvent> getItemLog() {
        return itemLog;
    }

    /**
     * Same behaviour as getAllItems, allows to filter returned items by name.
     * @param itemKey the item name
     * @return all items with a certain a hero has
     */
    public Set<Dota2Item> getItemsByName( String itemKey ) {
        return inventories.values().stream().flatMap( i -> i.getItems().stream() ).filter( i -> i.getKey().equals( itemKey ) ).collect( Collectors.toSet() );
    }

    public int getTotalGold() {
        return ((NavigableMap<Long, Integer>) gold).lastEntry().getValue();
    }

    public int getTotalXP() {
        return ((NavigableMap<Long, Integer>) xp).lastEntry().getValue();
    }

    public int getXP( long time ) {
        return ((NavigableMap<Long, Integer>) xp).floorEntry( time ).getValue();
    }

    public void setGold( long time, int gold ) {
        this.gold.put( time, gold );
    }

    public void setItem( long tickMs, int slot, Dota2Item newItem ) {
        if (inventories.containsKey( tickMs )) {
            //Just store the update
            inventories.get( tickMs ).getItems().set( slot, newItem );
        }
        else {
            //We advanced. Push the current bag configuration, calculate the diff to the previous one and make
            //a new array for the new tick
            final Entry<Long, UnitInventory> current = ((NavigableMap<Long, UnitInventory>) inventories).floorEntry( tickMs );
            if (current.getValue().getItems().get( slot ) == newItem) {
                //This update is actually not an update
                return;
            }
            final Entry<Long, UnitInventory> previous = ((NavigableMap<Long, UnitInventory>) inventories).lowerEntry( current.getKey() );
            final UnitInventory newBag = (UnitInventory) current.getValue().clone();
            newBag.getItems().set( slot, newItem );
            inventories.put( tickMs, newBag );

            //previous might be null if we actually pulled the 0l entry into current
            if (previous != null) {
                generateLogEntries( current.getKey(), previous.getValue(), current.getValue() );
            }
        }
    }

    public void setXP( long time, int xp ) {
        this.xp.put( time, xp );
    }

    @Override
    public String toString() {
        return "Hero [" + getKey() + "] (" + (isRadiant() ? "radiant" : "dire") + ')';
    }

    private void generateLogEntries( long tick, UnitInventory previous, UnitInventory current ) {
        for (int i = 0; i < previous.getItems().size(); i++) {
            if (previous.getItems().get( i ) != current.getItems().get( i )) {
                if (current.getItems().get( i ) != null) {
                    if (countItem( previous, current.getItems().get( i ) ) == 0) {
                        itemLog.add( new ItemEvent( tick, current.getItems().get( i ), i, true ) );
                    }
                }
                else if (previous.getItems().get( i ) != null) {
                    itemLog.add( new ItemEvent( tick, previous.getItems().get( i ), i, false ) );
                }
            }

        }
    }

}
