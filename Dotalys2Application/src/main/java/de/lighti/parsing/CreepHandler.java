package de.lighti.parsing;

import java.util.HashSet;
import java.util.Set;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Creep;
import de.lighti.model.game.Unit.LIFE_STATE;
import de.lighti.model.state.ParseState;

public class CreepHandler extends UnitTracker {

    private final Set<Creep> allCreeps;

    public CreepHandler( Dotalys2 app ) {
        super( app );

        allCreeps = new HashSet<Creep>();
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().contains( "Creep" )) {
            if (!activeUnits.containsKey( e.getId() )) {
                final Creep u = new Creep( e.getEntityClass().getName() + "_" + e.getId() + tickMs, e.getId() );
                final int team = (int) e.getProperty( "m_iTeamNum" );
                setTeam( u, team );

                final int x = (int) e.getProperty( Entity.CELL_X );
                final int y = (int) e.getProperty( Entity.CELL_Y );
                u.addX( tickMs, x );
                u.addY( tickMs, y );

                activeUnits.put( e.getId(), u );
            }
        }
        super.entityCreated( tickMs, e );
    }

    @Override
    public void entityRemoved( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().contains( "Creep" )) {
            final Creep u = (Creep) activeUnits.remove( e.getId() );
            //Might have gotten the remove update twice
            if (u != null) {
                allCreeps.add( u );
            }
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        super.entityUpdated( tickMs, e, name, oldValue );
        if (e.getEntityClass().getName().contains( "Creep" )) {
            final Creep u = (Creep) activeUnits.get( e.getId() );
            switch (name) {
                case "m_lifeState":
                    final LIFE_STATE oldState = LIFE_STATE.fromId( (Integer) oldValue );
                    final LIFE_STATE newState = LIFE_STATE.fromId( (Integer) e.getProperty( name ) );
                    if (oldState == LIFE_STATE.DYING && newState == LIFE_STATE.DEAD) {
                        u.addDeath( tickMs, (Integer) e.getProperty( Entity.CELL_X ), (Integer) e.getProperty( Entity.CELL_Y ) );
                    }
                    break;
                case Entity.CELL_X:
                    u.addX( tickMs, (Integer) e.getProperty( name ) );
                    break;

                case Entity.CELL_Y:
                    u.addY( tickMs, (Integer) e.getProperty( name ) );
                    break;
                case "m_iTeamNum":
                    final int team = (int) e.getProperty( name );
                    u.setRadiant( team == 2 ); //2 = Radiant, 3 = Dire, 5 = Spectator
                    break;
            }

        }

    }

    @Override
    public void parseComplete( long tickMs, ParseState state ) {
        appState.getReplay().setCreeps( allCreeps );

    }

}
