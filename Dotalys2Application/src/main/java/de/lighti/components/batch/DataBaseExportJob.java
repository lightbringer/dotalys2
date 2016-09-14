package de.lighti.components.batch;

import java.io.File;
import java.util.List;

import de.lighti.Dotalys2;
import de.lighti.io.data.DatabaseUtil;
import de.lighti.model.Replay;

public class DataBaseExportJob extends ExportJob {

    public DataBaseExportJob( Dotalys2 app, List<File> fileList ) {
        super( app, fileList );
    }

    @Override
    public void afterExport() {
        DatabaseUtil.close();
    }

    @Override
    public void beforeExport() {
        DatabaseUtil.init( app.getAppState().getProperties() );
    }

    @Override
    public void exportReplay( Replay r ) {
        DatabaseUtil.save( r );
    }

}
