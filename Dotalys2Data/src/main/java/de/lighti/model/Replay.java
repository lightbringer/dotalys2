package de.lighti.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.SortNatural;

import de.lighti.model.game.Ability;
import de.lighti.model.game.Creep;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;
import de.lighti.model.game.Roshan;
import de.lighti.model.game.Tower;

@Entity
@Access( AccessType.FIELD )
public class Replay {
    public enum GameState {
        DOTA_GAMERULES_STATE_DISCONNECT,
        /**
         * The state when hero selection is complete and player may spawn
         */
        DOTA_GAMERULES_STATE_PRE_GAME,
        /**
         * This state is reached after the 1 minute game time mark, when the horn sounds
         */
        DOTA_GAMERULES_STATE_GAME_IN_PROGRESS,

        /**
         * After loading is complete, the game switches into whatever hero selection mode this game has
         */
        DOTA_GAMERULES_STATE_HERO_SELECTION,
        DOTA_GAMERULES_STATE_INIT,
        DOTA_GAMERULES_STATE_LAST,
        DOTA_GAMERULES_STATE_POST_GAME,
        DOTA_GAMERULES_STATE_STRATEGY_TIME,
        DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD;

        /**
         * These values are taken from the game's DOTA_GameState struct
         * @param id
         * @return
         */
        public static GameState fromInternal( int id ) {
            switch (id) {
                case 7:
                    return DOTA_GAMERULES_STATE_DISCONNECT;
                case 5:
                    return DOTA_GAMERULES_STATE_GAME_IN_PROGRESS;
                case 2:
                    return DOTA_GAMERULES_STATE_HERO_SELECTION;
                case 0:
                    return DOTA_GAMERULES_STATE_INIT;
                case 9:
                    return DOTA_GAMERULES_STATE_LAST;
                case 6:
                    return DOTA_GAMERULES_STATE_POST_GAME;
                case 4:
                    return DOTA_GAMERULES_STATE_PRE_GAME;
                case 3:
                    return DOTA_GAMERULES_STATE_STRATEGY_TIME;
                case 1:
                    return DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private final static Logger LOGGER = Logger.getLogger( Replay.class.getName() );
    @Transient
    public TreeMap<Long, Map<String, Object>> gameEventsPerMs = new TreeMap<Long, Map<String, Object>>();
    //    private final SortedMap<String, Player> players = new TreeMap<String, Player>();
    @OneToMany( cascade = CascadeType.ALL )
    private final Set<Player> players;
    @Transient
    private final Set<String> playerVariables;
    @OneToMany( cascade = CascadeType.ALL )
    private final Set<Hero> heroes;
    @OneToOne( cascade = CascadeType.ALL )
    private Roshan roshan;
    @OneToMany( cascade = CascadeType.ALL )
    private final Set<Tower> towers;
    @OneToMany( cascade = CascadeType.ALL )
    private final Set<Dota2Item> allItems;
    @OneToMany( cascade = CascadeType.ALL )
    private final Set<Ability> allAbilities;
    @SortNatural
    @ElementCollection
    private final SortedMap<Long, GameState> gameStateChanges;

    private int msPerTick;

    @Id
    private final String name;

    @OneToMany( cascade = CascadeType.ALL )
    private Set<Encounter> encounters;

    private int gameVersion;
    @OneToMany( cascade = CascadeType.ALL )
    @JoinTable( name = "radiant_creeps" )
    private Set<Creep> radiantCreeps;
    @OneToMany( cascade = CascadeType.ALL )
    @JoinTable( name = "dire_creeps" )
    private Set<Creep> direCreeps;

    @Transient
    private final Map<Integer, Dota2Item> currentItems;
    @Transient
    private final Map<Integer, Ability> currentAbilities;

    public Replay( String name ) {
        this.name = name;
        playerVariables = new HashSet<String>();
        heroes = new HashSet<Hero>();
        allItems = new HashSet<Dota2Item>();
        currentItems = new HashMap<Integer, Dota2Item>();
        allAbilities = new HashSet<Ability>();
        currentAbilities = new HashMap<Integer, Ability>();
        players = new HashSet<Player>();
        gameStateChanges = new TreeMap<Long, GameState>();
        towers = new HashSet<Tower>();
        direCreeps = new HashSet<Creep>();
        radiantCreeps = new HashSet<Creep>();
        encounters = new HashSet<Encounter>();

        clear();
    }

    public void addAbility( int id, String ability ) {
        final Ability a = new Ability( ability );
        a.setEntityId( id );
        currentAbilities.put( id, a );
        allAbilities.add( a );

    }

    public void addEncounter( Encounter enc ) {
        encounters.add( enc );

    }

    public void addGameStateChange( GameState s, Long l ) {
        if (gameStateChanges.containsValue( s )) {
            LOGGER.warning( "We already seen state " + s + " with a different time mark " + l + ". not sure if the game can cycle through seen states. Discarding new value." );
            return;
        }
        gameStateChanges.put( l, s );
    }

    /**
     * Adds a new Hero to the app state. This method works as follows:
     * If a hero with the same name already exists, the entity id of <i>hero</i>
     * is added to the existing hero and <i>hero</i> is ignored. Else, <i>hero</i> is added to the game state
     *
     * For reasons unknown to me, the game re-creates the hero entity in the middle of the game play. To avoid
     * duplicates with a entity->hero mapping, I changed it so that hero->entity
     *
     * @param hero a hero entity
     */
    public void addHero( Hero hero ) {
        final Hero existingHero = heroes.stream().filter( h -> h.getKey().equals( hero.getKey() ) ).findAny().orElse( null );
        if (existingHero != null) {
            existingHero.getEntityIds().addAll( hero.getEntityIds() );
        }
        else {
            heroes.add( hero );
        }
    }

    public void addItem( int id, String value ) {
        final Dota2Item i = new Dota2Item( value );
        currentItems.put( id, i );
        allItems.add( i );
    }

    public void addPlayer( Player p ) {
        players.add( p );

    }

    public void addPlayerVariable( String n ) {
        if (!playerVariables.contains( n )) {
            playerVariables.add( n );
        }
    }

    public void addTower( Tower tower ) {
        final Tower existingTower = towers.stream().filter( t -> t.getKey().equals( tower.getKey() ) ).findAny().orElse( null );
        if (existingTower != null) {
            existingTower.getEntityIds().addAll( tower.getEntityIds() );
        }
        else {
            towers.add( tower );
        }
    }

    public void clear() {
        encounters.clear();
        playerVariables.clear();
        heroes.clear();
        allItems.clear();
        allAbilities.clear();
        players.clear();
        gameStateChanges.clear();
        gameStateChanges.put( 0l, GameState.DOTA_GAMERULES_STATE_INIT );
    }

    public Ability getAbility( int value ) {
//        if (!currentAbilities.containsKey( value )) {
//            throw new IllegalArgumentException( "ability " + value + " is not set up" );
//        }
        return currentAbilities.get( value );
    }

    public Set<Creep> getCreeps( boolean isRadiant ) {
        return isRadiant ? radiantCreeps : direCreeps;
    }

    /**
     * @return the last recorded game state (relative to when in the parsing process called, i.e.
     * the return value is not necessarily the state in which the game ended). For more precise values, use getGameStateTime instead
     */
    public GameState getCurrentGameState() {
        return gameStateChanges.get( gameStateChanges.lastKey() );
    }

    public Set<Encounter> getEncounters() {
        return Collections.unmodifiableSet( encounters );
    }

    /**
     * Returns the replay's length in miliseconds. Please
     * be aware that the this is the timestamp when the replay ended,
     * not when the game was decided, i.e. the throne was hit.
     *
     * @return the game's length in miliseconds
     */
    public long getGameLength() {
        return gameEventsPerMs.lastKey();
    }

    /**
     * Returns the ms (in replay time) when a certain game state was entered.
     *
     * @param s the game state
     * @return the time since server start, null if that game state was never reached.
     */
    public Long getGameStateTime( GameState s ) {
        final Map.Entry<Long, GameState> en = gameStateChanges.entrySet().stream().filter( e -> e.getValue() == s ).findAny().orElse( null );
        return en != null ? en.getKey() : null;
    }

    public int getGameVersion() {
        return gameVersion;
    }

    /**
     * Returns the hero that maps to the given entity id
     * @param entityId the id
     * @return the hero if any, or null
     */
    public Hero getHeroByEntity( int entityId ) {
        return heroes.stream().filter( h -> h.getEntityIds().contains( entityId ) ).findAny().orElse( null );
    }

    public Collection<Hero> getHeroes() {
        return Collections.unmodifiableCollection( heroes );
    }

    /**
     * The horn time in replay time (ms)
     * @return aka game start time (one minute after hero spawn)
     */
    public long getHornTime() {
        return getGameStateTime( GameState.DOTA_GAMERULES_STATE_GAME_IN_PROGRESS );
    }

    /**
     * Returns the Dota2Item corresponding to a (entity) id. The values in a
     * heroe's h_mItems array correspond to volatile entity ids representing the game item.
     * Hence we have to track the timestamp when a certain id was assigned to an item.
     * @param tick the timestamp
     * @param value the entity id of the corresponding CDOTA_Item entity
     * @return the Dota2Item
     */
    public Dota2Item getItem( int value ) {
        return currentItems.get( value );
    }

    public int getMsPerTick() {
        return msPerTick;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer( int id ) {
        return players.stream().filter( p -> p.getId() == id ).findAny().orElse( null );
    }

    public Player getPlayerByHero( Hero h ) {
        return players.stream().filter( p -> p.getHero() == h ).findAny().orElse( null );
    }

    public Player getPlayerByName( String player ) {
        return players.stream().filter( p -> p.getName().equals( player ) ).findAny().orElse( null );
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Roshan getRoshan() {
        return roshan;
    }

    public int getTeamGold( long time, boolean isRadiant ) {
        return (int) heroes.stream().filter( h -> h.isRadiant() == isRadiant ).collect( Collectors.summarizingInt( h -> h.getGold( time ) ) ).getSum();
    }

    public int getTeamXP( long time, boolean isRadiant ) {
        return (int) heroes.stream().filter( h -> h.isRadiant() == isRadiant ).collect( Collectors.summarizingInt( h -> h.getXP( time ) ) ).getSum();
    }

    public Tower getTower( int entity ) {
        return towers.stream().filter( t -> t.getEntityIds().contains( entity ) ).findAny().orElse( null );
    }

    public Collection<Tower> getTowers() {
        return Collections.unmodifiableCollection( towers );
    }

    public Set<String> getUnhandledPlayerVariableNames() {
        return playerVariables;
    }

    public void removeAbility( int id ) {
        currentAbilities.remove( id );
    }

    public void removeItem( int id ) {
        currentItems.remove( id );
    }

    public void setCreeps( Set<Creep> allCreeps ) {
        direCreeps = new HashSet<Creep>();
        radiantCreeps = new HashSet<Creep>();
        allCreeps.stream().forEach( u -> {
            if (!u.isNeutral()) {
                if (u.isRadiant()) {
                    radiantCreeps.add( u );
                }
                else {
                    direCreeps.add( u );
                }
            }
        } );

    }

    public void setEncounters( Set<Encounter> enc ) {
        encounters = enc;

    }

    public void setGameVersion( int protocolVersion ) {
        gameVersion = protocolVersion;

    }

    public void setMsPerTick( int msPerTick ) {
        this.msPerTick = msPerTick;
    }

    public void setRoshan( Roshan r ) {
        roshan = r;

    }

}
