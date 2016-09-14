package de.lighti.components.console;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.lighti.io.DataExporter;
import de.lighti.io.DataImporter;
import de.lighti.model.game.Creep;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Unit;

public class ConsoleComponent extends JScrollPane {
    private final static Pattern CREEP_NAME_SPLITTER = Pattern.compile( "(.*)_(\\d+)" );

    private final Set<Class<?>> ignoredSources;

    private final JTextPane textArea;

    private final Map<Unit, Style> styles;

    /**
     * Default constructor. Does not capture System.out and System.err
     */
    public ConsoleComponent() {
        this( false );
    }

    /**
     * @param captureSystemOut controls if System.out and System.err should be redirected to this component
     */
    public ConsoleComponent( boolean captureSystemOut ) {

        textArea = new JTextPane();
        textArea.setEditable( false );
        ignoredSources = new HashSet<Class<?>>();
        final DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );

        styles = new HashMap<Unit, Style>();

        setViewportView( textArea );

        if (captureSystemOut) {
            redirectSystemStreams();
        }
    }

    public void ignoreSources( Class<?> clazz ) {
        ignoredSources.add( clazz );
    }

    public void logEvent( long time, Unit u, String text ) {
        if (u == null || !ignoredSources.contains( u.getClass() )) {
            SwingUtilities.invokeLater( () -> logEventInternal( time, u, text ) );
        }
    }

    public void logEvents( Collection<LogEvent> events ) {
        if (events != null) {
            SwingUtilities.invokeLater( () -> {

                textArea.setIgnoreRepaint( true );
                for (final LogEvent l : events) {
                    if (l.u == null || !ignoredSources.contains( l.u.getClass() ) && (l.object == null || !ignoredSources.contains( l.object.getClass() ))) {
                        logEventInternal( l.time, l.u, l.event );
                    }
                }
                textArea.setIgnoreRepaint( false );
            } );
        }
    }

    public void removeIgnoreSources( Class<?> clazz ) {
        ignoredSources.remove( clazz );
    }

    private Style getStyle( Unit u ) {
        if (u == null || !(u instanceof Hero)) {
            return StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
        }
        else {
            Style s = styles.get( u );
            if (s == null) {
                s = (Style) StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE ).copyAttributes();
                final Color color = new Color( (float) Math.random(), (float) Math.random(), (float) Math.random(), 1f );
                StyleConstants.setForeground( s, color );
                styles.put( u, s );
            }
            return s;
        }
    }

    private void logEventInternal( long time, Unit u, String text ) {
        try {
            final DefaultStyledDocument doc = (DefaultStyledDocument) textArea.getStyledDocument();

            if (time >= 0) {
                doc.insertString( doc.getLength(), '[' + DataExporter.generateTimeCode( time, false ) + "] ", getStyle( null ) );
            }
            if (u != null) {
                if (u instanceof Creep) {
                    final Matcher m = CREEP_NAME_SPLITTER.matcher( u.getKey() );
                    m.find();
                    final String key = m.group( 1 );
                    final String id = m.group( 2 );
                    doc.insertString( doc.getLength(), DataImporter.getName( key ) + " (" + id + ")", getStyle( u ) );
                }
                else {
                    doc.insertString( doc.getLength(), DataImporter.getName( u.getKey() ), getStyle( u ) );
                }
            }

            doc.insertString( doc.getLength(), " " + text, getStyle( null ) );
            if (!text.endsWith( System.lineSeparator() )) {
                doc.insertString( doc.getLength(), System.lineSeparator(), getStyle( null ) );
            }
        }
        catch (final BadLocationException e) {
            throw new IllegalStateException( e );
        }
    }

    private void redirectSystemStreams() {

        final OutputStream out = new OutputStream() {
            @Override
            public void write( byte[] b ) throws IOException {
                write( b, 0, b.length );
            }

            @Override
            public void write( byte[] b, int off, int len ) throws IOException {
                logEvent( -1, null, new String( b, off, len ) );
            }

            @Override
            public void write( int b ) throws IOException {
                logEvent( -1, null, String.valueOf( (char) b ) );
            }
        };

        System.setOut( new PrintStream( out, true ) );
        System.setErr( new PrintStream( out, true ) );
    }

}
