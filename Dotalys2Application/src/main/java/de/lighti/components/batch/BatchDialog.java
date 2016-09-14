package de.lighti.components.batch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.lighti.DotaPlay.ProgressListener;
import de.lighti.Dotalys2;
import de.lighti.Dotalys2App;
import de.lighti.Main;
import de.lighti.components.ProgressDialog;
import de.lighti.components.options.OptionDialog;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;

public class BatchDialog extends JDialog implements Dotalys2 {

    private class SwitchModeActionListener implements ActionListener {

        @Override
        public void actionPerformed( ActionEvent e ) {
            final boolean isDatabaseMode = isDataBaseMode();

            getPropertyList().setEnabled( !isDatabaseMode );
            getBrowseDirectoryButton().setEnabled( !isDatabaseMode );
            getSavePathField().setEnabled( !isDatabaseMode );

            getConfigureDatabaseButton().setEnabled( isDatabaseMode );
            validateInput();
        }

    }

    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch (final Exception e) {
            // Don't care
        }
        final BatchDialog d = new BatchDialog( null );
        d.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent e ) {
                super.windowClosed( e );
                System.exit( 0 );
            }

        } );
        d.setVisible( true );
    }

    private final AppState state;

    private JList<File> fileList;
    private CheckBoxList propertyList;

    private JButton okButton;

    private JTextField savePathField;

    private JPanel saveToFilesPanel;

    private JRadioButton saveToFilesRadioButton;

    private JPanel saveToDatabasePanel;

    private JRadioButton saveToDatabaseRadioButton;

    private JButton configureDatabaseButton;
    private boolean maySave;
    private JButton browseDirectoryButton;

    /**
     *
     */
    private static final long serialVersionUID = 7655122816807766787L;

    public BatchDialog( Dotalys2App parent ) {
        super( parent, DataImporter.getName( "BATCH_EXPORT" ) );
        if (parent != null) {
            state = parent.getAppState();
        }
        else {
            state = new AppState( this );
            DataImporter.loadProperties( state );
        }

        setPreferredSize( new Dimension( 800, 600 ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setModal( true );
        setLayout( new GridBagLayout() );
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.weightx = .5;
        c.weighty = 1;
        add( createFilePanel(), c );
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = .45;
        c.gridheight = 1;
        add( getSaveToFilesPanel(), c );
        c.gridy = 1;
        add( getSaveToDatabasePanel(), c );
        c.gridy = 2;
        c.weighty = .1;
        c.fill = GridBagConstraints.NONE;

        add( getOkButton(), c );

        final ButtonGroup group = new ButtonGroup();
        group.add( getSaveToDatabaseRadioButton() );
        group.add( getSaveToFilesRadioButton() );
        pack();
    }

    private Component createFilePanel() {

        final JButton plusButton = new JButton( "+" );
        plusButton.addActionListener( e -> {
            final JFileChooser chooser = new JFileChooser( new File( "." ) );
            chooser.setMultiSelectionEnabled( true );
            chooser.setFileFilter( DataImporter.FILE_FILTER );
            final int returnVal = chooser.showOpenDialog( BatchDialog.this );
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final DefaultListModel<File> model = (DefaultListModel<File>) getFileList().getModel();
                for (final File f : chooser.getSelectedFiles()) {

                    model.addElement( f );
                }
                validateInput();
            }
        } );
        final JButton minusButton = new JButton( "-" );
        minusButton.addActionListener( e -> {
            final DefaultListModel<File> model = (DefaultListModel<File>) getFileList().getModel();

            for (final File f : getFileList().getSelectedValuesList()) {
                model.removeElement( f );
            }

        } );

        final JPanel filePanel = new JPanel();
        filePanel.setBorder( BorderFactory.createTitledBorder( DataImporter.getName( "FILES_TO_PROCESS" ) ) );

        filePanel.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.insets = new Insets( 5, 5, 5, 5 );
        filePanel.add( getFileList(), c );

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        filePanel.add( plusButton, c );
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.5;
        filePanel.add( minusButton, c );

        return filePanel;
    }

    @Override
    public void enableSave( boolean maySave ) {
        this.maySave = maySave;

    }

    private void export() {

        ExportJob exportJob;

        final List<File> fileList = Collections.list( ((DefaultListModel<File>) getFileList().getModel()).elements() );
        final int max = fileList.size();

        if (!isDataBaseMode()) {
            final File dir = new File( getSavePathField().getText() );
            if (!dir.exists() || !dir.isDirectory()) {
                handleError( new IllegalStateException( dir.getAbsolutePath() + " is not a writable directory" ) );
                return;
            }

            final List<CheckBoxListEntry> propertiesBoxes = getPropertyList().getSelectedValuesList();
            final List<String> properties = new ArrayList<String>();
            for (final CheckBoxListEntry e : propertiesBoxes) {
                properties.add( e.getValue() );
            }

            exportJob = new FileExportJob( fileList, dir, this, properties );
        }
        else {
            exportJob = new DataBaseExportJob( this, fileList );
        }

        final Thread t = new Thread( exportJob );
        t.start();
        final ProgressDialog pd = new ProgressDialog( BatchDialog.this );
        pd.setMaximum( max );
        final ProgressListener pl = position -> {
            pd.setValue( max - position );

            if (position <= 0) {
                pd.dispose();
            }
        };
        exportJob.setProgressListener( pl );
        pd.setVisible( true );

    }

    @Override
    public AppState getAppState() {
        return state;
    }

    private JButton getBrowseDirectoryButton() {
        if (browseDirectoryButton == null) {
            browseDirectoryButton = new JButton( DataImporter.getName( "BROWSE" ) );
            browseDirectoryButton.addActionListener( evt -> {
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                final int retVal = chooser.showSaveDialog( BatchDialog.this );
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    savePathField.setText( chooser.getSelectedFile().getAbsolutePath() );
                }
            } );

        }
        return browseDirectoryButton;
    }

    private JButton getConfigureDatabaseButton() {
        if (configureDatabaseButton == null) {
            configureDatabaseButton = new JButton( DataImporter.getName( "CONFIGURE" ) );
            configureDatabaseButton.setEnabled( false );
            configureDatabaseButton.addActionListener( l -> new OptionDialog( this ).setVisible( true ) );
        }
        return configureDatabaseButton;
    }

    private JList<File> getFileList() {
        if (fileList == null) {
            fileList = new JList<File>( new DefaultListModel<File>() );
            fileList.setBorder( BorderFactory.createLoweredBevelBorder() );
        }

        return fileList;
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton( DataImporter.getName( "OK" ) );
            okButton.setEnabled( false );
            okButton.addActionListener( e -> export() );
        }

        return okButton;
    }

    private CheckBoxList getPropertyList() {
        if (propertyList == null) {
            propertyList = new CheckBoxList();
            propertyList.setBorder( BorderFactory.createLoweredBevelBorder() );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "CELLS" ), false ) );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "MOVEMENT" ), false ) );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "ZONES" ), false ) );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "ABILITIES" ), false ) );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "ITEMS" ), false ) );
            ((DefaultListModel<CheckBoxListEntry>) propertyList.getModel()).addElement( new CheckBoxListEntry( DataImporter.getName( "ENCOUNTER" ), false ) );
            propertyList.addPropertyChangeListener( arg0 -> validateInput() );
        }
        return propertyList;
    }

    private JTextField getSavePathField() {
        if (savePathField == null) {
            savePathField = new JTextField( new File( "." ).getAbsolutePath() );
            savePathField.getDocument().addDocumentListener( new DocumentListener() {

                @Override
                public void changedUpdate( DocumentEvent e ) {
                    validateInput();
                }

                @Override
                public void insertUpdate( DocumentEvent e ) {
                }

                @Override
                public void removeUpdate( DocumentEvent e ) {
                }
            } );
        }
        return savePathField;
    }

    private JPanel getSaveToDatabasePanel() {
        if (saveToDatabasePanel == null) {
            saveToDatabasePanel = new JPanel();
            saveToDatabasePanel.setBorder( BorderFactory.createTitledBorder( DataImporter.getName( "SAVE_TO_DATABASE" ) ) );
            saveToDatabasePanel.setLayout( new GridBagLayout() );
            final GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets( 5, 5, 5, 5 );
            c.anchor = GridBagConstraints.NORTHWEST;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = .5;
            saveToDatabasePanel.add( getSaveToDatabaseRadioButton(), c );
            c.anchor = GridBagConstraints.CENTER;
            c.gridy = 1;
            saveToDatabasePanel.add( getConfigureDatabaseButton(), c );
        }

        return saveToDatabasePanel;
    }

    private JRadioButton getSaveToDatabaseRadioButton() {
        if (saveToDatabaseRadioButton == null) {
            saveToDatabaseRadioButton = new JRadioButton( DataImporter.getName( "SAVE_TO_DATABASE" ) );
            saveToDatabaseRadioButton.addActionListener( new SwitchModeActionListener() );

        }

        return saveToDatabaseRadioButton;
    }

    private JPanel getSaveToFilesPanel() {
        if (saveToFilesPanel == null) {
            saveToFilesPanel = new JPanel() {

                @Override
                public void setEnabled( boolean enabled ) {
                    super.setEnabled( enabled );
                    getPropertyList().setEnabled( enabled );
                }

            };
            saveToFilesPanel.setBorder( BorderFactory.createTitledBorder( DataImporter.getName( "SAVE_TO_FILES" ) ) );

            saveToFilesPanel.setLayout( new GridBagLayout() );
            final GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets( 0, 5, 0, 5 );
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = .25;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.NONE;
            saveToFilesPanel.add( getSaveToFilesRadioButton(), c );
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 3;
            c.gridy = 1;
            c.weighty = 2;
            saveToFilesPanel.add( getPropertyList(), c );
            c.weighty = .25;
            c.fill = GridBagConstraints.NONE;
            c.gridy = 2;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.weightx = .1;
            final JLabel label = new JLabel( DataImporter.getName( "SAVE_TO" ) );
            saveToFilesPanel.add( label, c );
            c.gridx = 1;
            c.weightx = .45;
            c.fill = GridBagConstraints.HORIZONTAL;
            saveToFilesPanel.add( getSavePathField(), c );
            c.gridx = 2;
            c.fill = GridBagConstraints.NONE;
            saveToFilesPanel.add( getBrowseDirectoryButton(), c );

        }
        return saveToFilesPanel;

    }

    private JRadioButton getSaveToFilesRadioButton() {
        if (saveToFilesRadioButton == null) {
            saveToFilesRadioButton = new JRadioButton( DataImporter.getName( "SAVE_TO_FILES" ) );
            saveToFilesRadioButton.setSelected( true );
            saveToFilesRadioButton.addActionListener( new SwitchModeActionListener() );

        }

        return saveToFilesRadioButton;
    }

    @Override
    public void handleError( Throwable t ) {
        Main.displayException( t );

    }

    private boolean isDataBaseMode() {
        return getSaveToDatabaseRadioButton().isSelected();
    }

    private void validateInput() {
        boolean ret = true;
        ret &= getFileList().getModel().getSize() > 0;
        if (!isDataBaseMode()) {
            ret &= !getPropertyList().getSelectedValuesList().isEmpty();
            ret &= !getSavePathField().getText().isEmpty();
        }
        else {
            ret &= maySave;
        }
        getOkButton().setEnabled( ret );
    }

}
