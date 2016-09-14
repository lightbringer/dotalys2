package de.lighti.parsing;

import java.util.logging.Logger;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Dota2Item;

public class ItemTracker extends AbstractGameEventTracker {

    private final static Logger LOGGER = Logger.getLogger( ItemTracker.class.getName() );

    public ItemTracker( Dotalys2 app ) {
        super( app );
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        if (e.getEntityClass().getName().startsWith( "CDOTA_Item" )) {
            final String key = e.getName();

            appState.getReplay().addItem( e.getId(), key );
        }
    }

    @Override
    public void entityRemoved( long tickMs, Entity removed ) {
        super.entityRemoved( tickMs, removed );
        if (removed.getEntityClass().getName().startsWith( "CDOTA_Item" )) {

            appState.getReplay().removeItem( removed.getId() );
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        final Dota2Item item = appState.getReplay().getItem( e.getId() );
        if (item != null) {
            if (name.equals( Entity.NAME_INDEX )) {
                item.setKey( e.getName() );
            }
            else if (name.equals( "m_fCooldown" )) {
                float cooldownEnd = (float) e.getProperty( "m_fCooldown" );
                final float cooldown = (float) e.getProperty( "m_flCooldownLength" );
                if (cooldown > 0) {
                    //TODO find out why cooldownEnd may be 0 here
                    if (cooldownEnd - cooldown < 0) {
                        LOGGER.warning( "Item usage timestamp invalid for item " + item + ". Assuming tick " + tickMs );
                        cooldownEnd = tickMs + cooldown;
                    }
                    //Cooldown stamps are in seconds. Don't forget to multiply by 1000
                    item.addUsage( (long) ((cooldownEnd - cooldown) * 1000l) );
                }
            }
        }
    }

}
