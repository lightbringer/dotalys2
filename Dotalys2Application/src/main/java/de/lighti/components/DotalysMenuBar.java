package de.lighti.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.lighti.Dotalys2App;
import de.lighti.Main;
import de.lighti.components.batch.BatchDialog;
import de.lighti.components.options.OptionDialog;
import de.lighti.io.DataImporter;
import de.lighti.io.data.DatabaseUtil;
import de.lighti.model.AppState;
import de.lighti.model.Replay;
import de.lighti.model.Statics;

public class DotalysMenuBar extends JMenuBar {
    /**
     *
     */
    private static final long serialVersionUID = -152856153942387447L;

    private final Dotalys2App owner;

    private JMenu fileMenu;
    private JMenuItem fileOpenMenuItem;

    private JMenuItem batchExportMenuItem;

    private JMenuItem aboutMenuItem;

    private JMenuItem fileSaveDatabaseMenuItem;

    private JMenu functionsMenu;

    private JMenuItem optionsMenuItem;

    public DotalysMenuBar( Dotalys2App o ) {
        super();
        setLayout( new GridBagLayout() );
        owner = o;

        add( getFileMenu() );
        add( getFunctionsMenu() );
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add( Box.createHorizontalGlue(), c );

    }

    public JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu( DataImporter.getName( "FILE" ) );
            fileMenu.add( getFileOpenMenuItem() );
            fileMenu.add( getFileSaveDatabaseMenuItem() );
        }
        return fileMenu;
    }

    public JMenuItem getFileOpenMenuItem() {
        if (fileOpenMenuItem == null) {
            fileOpenMenuItem = new JMenuItem();
            fileOpenMenuItem.setAction( new AbstractAction() {
                @Override
                public void actionPerformed( ActionEvent e ) {

                    // Create a file chooser
                    final JFileChooser fc = new JFileChooser( "." );
                    fc.setFileFilter( DataImporter.FILE_FILTER );

                    // In response to a button click:
                    final int returnVal = fc.showOpenDialog( owner );
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final AppState appState = owner.getAppState();
                        appState.setReplay( new Replay( fc.getSelectedFile().getName() ) );
                        final ProgressDialog pd = new ProgressDialog( owner );
                        final long fs = fc.getSelectedFile().length();
                        pd.setMaximum( fs );
                        final Thread t = new Thread( () -> {
                            try {
                                DataImporter.parseReplayFile( owner, fc.getSelectedFile(), position -> pd.setValue( fs - position ) );
                                pd.setVisible( false );
                                owner.setTitle( DataImporter.getName( "APPLICATION_TITLE" ) + " " + fc.getSelectedFile().getName() );
                            }
                            catch (final Exception ex) {
                                Main.displayException( ex );
                            }
                        } );
                        t.start();
                        pd.setVisible( true );
                    }
                }
            } );
            fileOpenMenuItem.setText( "Open" );
        }

        return fileOpenMenuItem;
    }

    public JMenuItem getFileSaveDatabaseMenuItem() {
        if (fileSaveDatabaseMenuItem == null) {
            fileSaveDatabaseMenuItem = new JMenuItem() {
                @Override
                public void setEnabled( boolean enabled ) {
                    super.setEnabled( enabled );
                    fileSaveDatabaseMenuItem.setToolTipText( enabled ? "" : DataImporter.getName( "MENU_ITEM_SAVE_DATABASE_TOOLTIP" ) );
                }
            };
            fileSaveDatabaseMenuItem.setAction( new AbstractAction() {
                @Override
                public void actionPerformed( ActionEvent e ) {
                    try {
                        DatabaseUtil.init( owner.getAppState().getProperties() );
                        DatabaseUtil.save( owner.getAppState().getReplay() );
                    }
                    catch (final Exception ex) {
                        owner.handleError( ex );
                    }
                }
            } );
            fileSaveDatabaseMenuItem.setEnabled( false );
            fileSaveDatabaseMenuItem.setText( DataImporter.getName( "SAVE_TO_DATABASE" ) );
        }
        return fileSaveDatabaseMenuItem;
    }

    public JMenu getFunctionsMenu() {
        if (functionsMenu == null) {
            functionsMenu = new JMenu( DataImporter.getName( "FUNCTIONS" ) );
            functionsMenu.add( getBatchExportItem() );
            functionsMenu.add( getOptionsMenuItem() );
            functionsMenu.addSeparator();
            functionsMenu.add( getAboutMenuItem() );

        }
        return functionsMenu;
    }

    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();

            aboutMenuItem.setAction( new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = -7625044491823967L;

                @Override
                public void actionPerformed( ActionEvent e ) {
                    final String message = String.format( DataImporter.getName( "ABOUT_DOTALYS" ), Statics.DOTALYS_VERSION );
                    JOptionPane.showMessageDialog( owner, message, DataImporter.getName( "ABOUT" ), JOptionPane.INFORMATION_MESSAGE );

                }
            } );
            aboutMenuItem.setText( "About" );
        }
        return aboutMenuItem;
    }

    private JMenuItem getBatchExportItem() {
        if (batchExportMenuItem == null) {
            batchExportMenuItem = new JMenuItem( DataImporter.getName( "BATCH_EXPORT" ) );
            batchExportMenuItem.addActionListener( arg0 -> new BatchDialog( owner ).setVisible( true ) );
        }

        return batchExportMenuItem;
    }

    private JMenuItem getOptionsMenuItem() {
        if (optionsMenuItem == null) {
            optionsMenuItem = new JMenuItem( DataImporter.getName( "OPTIONS" ) );
            optionsMenuItem.addActionListener( arg0 -> new OptionDialog( owner ).setVisible( true ) );
        }

        return optionsMenuItem;
    }
}
