package de.lighti;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import de.lighti.model.CombatLogEvent;
import de.lighti.model.SkadiEntity;
import de.lighti.model.state.ParseState;
import skadistats.clarity.decoder.s2.S2DTClass;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.entities.OnEntityDeleted;
import skadistats.clarity.processor.entities.OnEntityUpdated;
import skadistats.clarity.processor.entities.UsesEntities;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.reader.OnMessage;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.processor.stringtables.OnStringTableEntry;
import skadistats.clarity.processor.tempentities.OnTempEntity;
import skadistats.clarity.source.InputStreamSource;
import skadistats.clarity.wire.common.proto.NetMessages;

@UsesEntities
public class SkadiDotaPlay implements DotaPlay, ParseState {

    public final static Map<Integer, String> entityNames = new HashMap<Integer, String>();
    private final List<GameEventListener> listeners;
    private float tickrate;
    private int protocolVersion;
    private final SkadiEntity[] entities = new SkadiEntity[1 << 17];

    private FileInputStream stream;

    private ProgressListener[] progressListener;

    public SkadiDotaPlay() {
        listeners = new ArrayList<GameEventListener>();
    }

    @Override
    public void addListener( GameEventListener l ) {
        listeners.add( l );

    }

    @Override
    public de.lighti.model.Entity getEntity( int id ) {
        return entities[id];
    }

    @Override
    public Collection<GameEventListener> getListeners() {
        return listeners;
    }

    @Override
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public float getTickInterval() {
        return tickrate;
    }

    @Override
    public void loadFile( String absolutePath, ProgressListener... listeners ) {

        progressListener = listeners;

        try {
            stream = new FileInputStream( new File( absolutePath ) );
            final SimpleRunner runner = new SimpleRunner( new InputStreamSource( stream ) );
            final Context ctx = runner.runWith( this ).getContext();
            for (final GameEventListener l : this.listeners) {
                l.parseComplete( (long) (ctx.getTick() * 1000 * getTickInterval()), this );
            }
        }
        catch (final Exception e) {
//            Logger.getLogger( SkadiDotaPlay.class.getName() ).severe( e.getLocalizedMessage() );
//            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    @OnCombatLogEntry
    public void onCombatLogEvent( Context ctx, CombatLogEntry e ) {
        for (final GameEventListener l : listeners) {
            l.gameEvent( (long) (ctx.getTick() * 1000 * getTickInterval()), new CombatLogEvent( e ) );
        }

    }

    @OnEntityCreated
    public void onEntityCreate( Context ctx, Entity entity ) {
        final SkadiEntity e = new SkadiEntity( entity );

        final List<FieldPath> entries = new ArrayList<FieldPath>();
        ((S2DTClass) entity.getDtClass()).getSerializer().collectFieldPaths( new FieldPath(), entries, entity.getState() );

        for (final FieldPath fp : entries) {
            final String name = entity.getDtClass().getNameForFieldPath( fp );
            final Object value = entity.getDtClass().getValueForFieldPath( fp, entity.getState() );
            e.setProperty( name, value );
        }
        entities[entity.getIndex()] = e;
        for (final GameEventListener l : listeners) {
            l.entityCreated( (long) (ctx.getTick() * 1000 * getTickInterval()), e );
        }
    }

    @OnEntityDeleted
    public void onEntityDelete( Context ctx, Entity entity ) {
        final SkadiEntity removed = entities[entity.getIndex()];
        entities[entity.getIndex()] = null;

        for (final GameEventListener l : listeners) {
            l.entityRemoved( (long) (ctx.getTick() * 1000 * getTickInterval()), removed );
        }
    }

    @OnEntityUpdated
    public <T> void onEntityUpdate( Context ctx, Entity entity, FieldPath[] fp, int nChanged ) {
        final Map<String, Object> oldValues = new HashMap<String, Object>();

        final SkadiEntity updated = entities[entity.getIndex()];
        String name = null;
        for (int i = 0; i < nChanged; i++) {
            try {
                final FieldPath p = fp[i];
                name = entity.getDtClass().getNameForFieldPath( p );
                final Object old = updated.getProperty( name );
                final Object newO = entity.getProperty( name );

                oldValues.put( name, old );
                updated.setProperty( name, newO );
            }
            catch (final Exception | Error e) {
                Logger.getLogger( SkadiDotaPlay.class.getName() ).log( Level.WARNING,
                                "Error on entity update (" + entity.getDtClass().getDtName() + "->" + name + ") " + e.getMessage() );
                Logger.getLogger( SkadiDotaPlay.class.getName() ).log( Level.FINEST, "Stacktrace follows", e );
            }
        }
        for (final Map.Entry<String, Object> e : oldValues.entrySet()) {
            for (final GameEventListener l : listeners) {
                l.entityUpdated( (long) (ctx.getTick() * 1000 * getTickInterval()), updated, e.getKey(), e.getValue() );
            }
        }

    }

    @OnMessage( NetMessages.CSVCMsg_ServerInfo.class )
    public void onFileInfo( Context ctx, NetMessages.CSVCMsg_ServerInfo message ) throws IOException {
        setProtocolVersion( message.getProtocol() );
        setTickRate( message.getTickInterval() );
    }

    @OnStringTableEntry( "EntityNames" )
    public void onStringTableEntry( Context ctx, StringTable stringTable, int rowIndex, String key, ByteString bs ) {
        entityNames.put( rowIndex, key );
    }

    @OnTempEntity
    public void onTempEntity( Context ctx, Entity temp ) {
        for (final GameEventListener l : listeners) {
            l.entityCreated( (long) (ctx.getTick() * 1000 * getTickInterval()), new SkadiEntity( temp ) );
        }
    }

    @OnTickEnd
    public void onTickEnd( Context ctx, boolean synthetic ) {
        if (progressListener != null) {
            for (final ProgressListener p : progressListener) {
                try {
                    p.bytesRemaining( stream.available() );
                }
                catch (final IOException e) {
                    throw new RuntimeException( e );
                }
            }
        }
        for (final GameEventListener l : listeners) {
            l.tickEnd( (long) (ctx.getTick() * 1000 * getTickInterval()), this );
        }
    }

    private void setProtocolVersion( int networkProtocol ) {
        protocolVersion = networkProtocol;

    }

    private void setTickRate( float f ) {
        tickrate = f;

    }

}
