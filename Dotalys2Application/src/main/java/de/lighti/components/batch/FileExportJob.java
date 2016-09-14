package de.lighti.components.batch;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lighti.Dotalys2;
import de.lighti.io.ChartCreator;
import de.lighti.io.DataExporter;
import de.lighti.io.DataImporter;
import de.lighti.model.Replay;
import de.lighti.model.game.Player;

public class FileExportJob extends ExportJob {

    private static final int MAX_LENGTH = 127;

    private static final Pattern PATTERN = Pattern.compile( "[^A-Za-z0-9_]" );

    private static String escapeUrlAsFilename( String url ) {

        final StringBuffer sb = new StringBuffer();

        // Apply the regex.
        final Matcher m = PATTERN.matcher( url );

        while (m.find()) {
            m.appendReplacement( sb,

                            // Convert matched character to percent-encoded.
                            "%" + Integer.toHexString( m.group().charAt( 0 ) ).toUpperCase() );
        }
        m.appendTail( sb );

        final String encoded = sb.toString();

        // Truncate the string.
        final int end = Math.min( encoded.length(), MAX_LENGTH );
        return encoded.substring( 0, end );
    }

    private final File outputDir;

    private final Collection<String> properties;

    public FileExportJob( List<File> fileList, File outputDir, Dotalys2 app, Collection<String> properties ) {
        super( app, fileList );

        this.outputDir = outputDir;
        this.properties = properties;
    }

    @Override
    public void exportReplay( Replay r ) {

        for (final String entry : properties) {
            for (final Player p : r.getPlayers()) {
                final boolean export = true;
                final String fileOut = outputDir.getAbsolutePath() + "/" + r.getName() + "_" + entry + "_" + escapeUrlAsFilename( p.getName() ) + ".csv";
                String header;
                String[][] data;

                if (entry.equals( DataImporter.getName( "MOVEMENT" ) )) {
                    header = "#tickms, x , y";
                    data = ChartCreator.createMoveLog( p.getName(), r, false );
                }
                else if (entry.equals( DataImporter.getName( "CELLS" ) )) {
                    header = "#tickms, x , y";
                    data = ChartCreator.createMoveLog( p.getName(), r, true );
                }
                else if (entry.equals( DataImporter.getName( "ZONES" ) )) {
                    header = "#tickms, zone";
                    data = ChartCreator.createZoneLog( p.getName(), r );
                }
                else if (entry.equals( DataImporter.getName( "ABILITIES" ) )) {
                    header = "#tickms, x, y, ability";
                    data = ChartCreator.createAbilityLog( p );
                }
                else if (entry.equals( DataImporter.getName( "ITEMS" ) )) {
                    header = "#tickms, x, y, item";
                    data = ChartCreator.createItemLog( p );
                }
//                else if (entry.equals( DataImporter.getName( "ENCOUNTER" ) )) {
//                    //exportEncounter exports data for all players, so only execute this once
//                    exportEncounter( r, outputDir );
//                    export = false;
//                    header = null;
//                    data = null;
//                }
                else {
                    throw new RuntimeException( "Unknown property " + entry );
                }

                if (export) {
                    exportData( header, data, fileOut );
                }
                else {
                    break;
                }
            }

        }
    }

    private void exportData( String header, String[][] data, String fileOut ) {
        final File file = new File( fileOut );

        try {
            DataExporter.exportCSV( file, header, data );
        }
        catch (final IOException e) {
            app.handleError( e );
        }

    }

//    private void exportEncounter( Replay r, File outputDir ) {
//        for (final Encounter e : r.getEncounters()) {
//
//            try {
//                DataExporter.exportEncounter( new File( outputDir.getAbsolutePath() + "/" + r.getName() + "_" + DataImporter.getName( "ENCOUNTER" ) + "_"
//                                + e.getLastTimestamp() + "_" + e.hashCode() + ".csv" ), r, e );
//            }
//            catch (final IOException ex) {
//                app.handleError( ex );
//            }
//        }
//
//    }

}
