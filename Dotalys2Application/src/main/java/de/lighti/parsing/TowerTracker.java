package de.lighti.parsing;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Tower;

public class TowerTracker extends AbstractGameEventTracker {

    public TowerTracker( Dotalys2 app ) {
        super( app );
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().equals( "CDOTA_BaseNPC_Tower" )) {
            final String name = e.getName();
            final Tower t = new Tower( name, e.getId() );
            appState.getReplay().addTower( t );

            final int x = (int) e.getProperty( Entity.CELL_X );
            final int y = (int) e.getProperty( Entity.CELL_Y );
            t.addX( 0, x ); //use 0 (towers are there from the start) to avoid glitches
            t.addY( 0, y );
        }

    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        if (e.getEntityClass().getName().equals( "CDOTA_BaseNPC_Tower" )) {
            final Tower t = appState.getReplay().getTower( e.getId() );

            switch (name) {
                case Entity.NAME_INDEX:
                    t.setKey( e.getName() );
                    break;
                case "m_iHealth":

                    final int health = (int) e.getProperty( name );
                    if (health <= 0) {
                        t.addDeath( tickMs, (Integer) e.getProperty( Entity.CELL_X ), (Integer) e.getProperty( Entity.CELL_Y ) );
                    }
                    break;
            }
        }
    }

}
