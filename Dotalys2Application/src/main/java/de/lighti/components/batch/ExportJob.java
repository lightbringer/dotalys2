package de.lighti.components.batch;

import java.io.File;
import java.util.List;

import de.lighti.DotaPlay.ProgressListener;
import de.lighti.Dotalys2;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;
import de.lighti.model.Replay;

public abstract class ExportJob implements Runnable {

    protected final Dotalys2 app;
    protected ProgressListener progressListener;
    protected final List<File> fileList;

    protected ExportJob( Dotalys2 app, List<File> fileList ) {
        super();
        this.app = app;
        this.fileList = fileList;
    }

    public void afterExport() {
    }

    public void beforeExport() {
    };

    public abstract void exportReplay( Replay r );;

    @Override
    public void run() {
        beforeExport();
        int i = fileList.size();
        for (final File f : fileList) {

            final AppState state = app.getAppState();
            state.setReplay( new Replay( f.getName() ) );
            try {
                DataImporter.parseReplayFile( app, f );
                exportReplay( state.getReplay() );
                i--;
                if (progressListener != null) {
                    progressListener.bytesRemaining( i );
                }
            }
            catch (final Exception e) {
                if (progressListener != null) {
                    progressListener.bytesRemaining( 0 );
                }
                app.handleError( e );
                break;
            }

        }
        afterExport();
    }

    public void setProgressListener( ProgressListener progressListener ) {
        this.progressListener = progressListener;
    }

}
