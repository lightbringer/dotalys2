package de.lighti;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class Main {
    public static void displayException( Throwable e ) {

        final StringWriter errors = new StringWriter();
        e.printStackTrace( new PrintWriter( errors ) );

        Logger.getLogger( Main.class.getName() ).severe( e.getLocalizedMessage() );
        e.printStackTrace( System.err );
        JOptionPane.showMessageDialog( null, errors.toString(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE );
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException {

        try {
            new Dotalys2App().setVisible( true );
        }
        catch (final Exception e) {
            displayException( e );
        }
    }
}
