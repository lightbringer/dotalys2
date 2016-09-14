package de.lighti.parsing;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Roshan;
import de.lighti.model.game.Unit.LIFE_STATE;

/**
 * This tracker does pretty much the same as HeroTracker. For the sake of code clarity, we gave
 * Roshan his own tracker.
 *
 * @author Tobias Mahlmann
 *
 */
public class RoshanTracker extends UnitTracker {

    public RoshanTracker( Dotalys2 app ) {
        super( app );
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {

        if (e.getEntityClass().getName().equals( "CDOTA_Unit_Roshan" )) {
            Roshan r = appState.getReplay().getRoshan();

            if (r == null) {

                r = new Roshan(); //Poor Roshan has no name
                activeUnits.put( e.getId(), r );
                appState.getReplay().setRoshan( r );

                final int x = (int) e.getProperty( Entity.CELL_X );
                final int y = (int) e.getProperty( Entity.CELL_Y );
                r.addX( 0, x ); //use 0 (roshan is there from the start) to avoid glitches
                r.addY( 0, y );
            }
            else {

                r.getEntityIds().add( e.getId() );

            }
        }
        super.entityCreated( tickMs, e );
    }

    @Override
    public void entityRemoved( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().equals( "CDOTA_Unit_Roshan" )) {
            activeUnits.remove( e.getId() );
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        super.entityUpdated( tickMs, e, name, oldValue );
        if (e.getEntityClass().getName().equals( "CDOTA_Unit_Roshan" )) {
            final Roshan r = appState.getReplay().getRoshan();

            switch (name) {
                case Entity.CELL_X:
                    r.addX( tickMs, (Integer) e.getProperty( name ) );
                    break;
                case Entity.CELL_Y:
                    r.addY( tickMs, (Integer) e.getProperty( name ) );
                    break;
                case "m_lifeState":
                    final LIFE_STATE oldState = LIFE_STATE.fromId( (Integer) oldValue );
                    final LIFE_STATE newValue = LIFE_STATE.fromId( (int) e.getProperty( name ) );
                    if (newValue == LIFE_STATE.ALIVE && oldState != LIFE_STATE.ALIVE) {
                        r.addRespawn( tickMs );
                    }
                    else if (oldState == LIFE_STATE.DYING && newValue == LIFE_STATE.DEAD) {
                        r.addDeath( tickMs, (Integer) e.getProperty( Entity.CELL_X ), (Integer) e.getProperty( Entity.CELL_Y ) );
                    }
                    break;
            }
//            else if (name.contains( "m_hItems" )) {
//                final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
//                int value = (int) e.getProperty( name ).getValue();
//                if (value != 0x1FFFFF) {
//                    value &= 0x7ff;
//                    setItemInCache( h, tickMs, slot, value );
//                }
//                else {
//                    setItemInCache( h, tickMs, slot, null );
//                }
//            }
//            else if (name.contains( "m_hAbilities" )) {
//                int value = (int) e.getProperty( name ).getValue();
//                if (value != 0x1FFFFF) {
//
////                    final int slot = Integer.parseInt( name.substring( name.lastIndexOf( "." ) + 1 ) );
//                    value &= 0x7ff;
//                    setAbilityInCache( h, tickMs, value );
//                }
//            }
        }
    }

}
