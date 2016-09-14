package de.lighti.parsing;

import de.lighti.Dotalys2;
import de.lighti.model.Entity;
import de.lighti.model.game.Ability;

public class AbilityTracker extends AbstractGameEventTracker {

    public AbilityTracker( Dotalys2 app ) {
        super( app );
    }

    @Override
    public void entityCreated( long tickMs, Entity e ) {
        super.entityCreated( tickMs, e );
        final String className = e.getEntityClass().getName();
        if (className.contains( "Ability" )) {
            if (appState.getReplay().getAbility( e.getId() ) == null) {
                appState.getReplay().addAbility( e.getId(), e.getName() );

            }
        }
    }

    @Override
    public void entityRemoved( long tickMs, Entity e ) {
        super.entityRemoved( tickMs, e );
        if (e.getEntityClass().getName().contains( "Ability" )) {
            appState.getReplay().removeAbility( e.getId() );
        }
    }

    @Override
    public <T> void entityUpdated( long tickMs, Entity e, String name, T oldValue ) {
        if (e.getEntityClass().getName().contains( "Ability" )) {
            final Ability a = appState.getReplay().getAbility( e.getId() );

            if (name.equals( Entity.NAME_INDEX )) {
                a.setKey( e.getName() );
            }
            else if (name.equals( "m_iLevel" )) {
                a.setLevel( tickMs, (Integer) e.getProperty( name ) );
            }
            else if (name.equals( "m_fCooldown" )) {
                final Float value = (Float) e.getProperty( name );
                if (value > 0 && !value.equals( oldValue )) {
                    final Float cd = (Float) e.getProperty( "m_flCooldownLength" );
                    final long time = (long) ((value - cd) * 1000f);
                    appState.getReplay().getAbility( e.getId() ).addInvocation( time );
                }
            }

        }
    }
}
