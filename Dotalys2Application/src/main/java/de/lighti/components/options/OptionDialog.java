package de.lighti.components.options;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.lighti.Dotalys2;
import de.lighti.io.DataImporter;
import de.lighti.model.AppState;

public class OptionDialog extends JDialog {
    private final AppState state;
    private JPanel databasePanel;
    private JTextField databaseField;
    private JTextField databasePasswordField;
    private JTextField databaseUserField;
    private JTextField databaseServerField;
    private JCheckBox databaseClearField;
    private JButton okButton;
    private JButton cancelButton;

    public OptionDialog( Window owner ) {
        super( owner, DataImporter.getName( "OPTIONS" ), ModalityType.APPLICATION_MODAL );

        setSize( new Dimension( 400, 300 ) );
        state = ((Dotalys2) owner).getAppState();
        setLayout( new GridBagLayout() );
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets( 10, 10, 10, 10 );
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        add( getDatabasePanel(), c );
        c.weighty = 1.0; //request any extra vertical space
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        add( getOkButton(), c );
        c.gridx = 1;
        add( getCancelButton(), c );
    }

    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton( DataImporter.getName( "CANCEL" ) );
            cancelButton.addActionListener( l -> OptionDialog.this.dispose() );
        }
        return cancelButton;
    }

    private JCheckBox getClearDatabaseField() {
        if (databaseClearField == null) {
            databaseClearField = new JCheckBox( "", state.isClearDatabase() );
        }

        return databaseClearField;
    }

    private JTextField getDatabaseField() {
        if (databaseField == null) {
            databaseField = new JTextField( state.getDatabase() );
        }

        return databaseField;
    }

    public JPanel getDatabasePanel() {
        if (databasePanel == null) {
            databasePanel = new JPanel( new GridBagLayout() );
            databasePanel.setBorder( BorderFactory.createTitledBorder( DataImporter.getName( "DATABASE" ) ) );
            final GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets( 0, 5, 0, 5 );
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            c.weighty = 0;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.LINE_START;
            databasePanel.add( new JLabel( DataImporter.getName( "SERVER" ) + ":" ), c );
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 1;
            databasePanel.add( getDatabaseServerField(), c );
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            databasePanel.add( new JLabel( DataImporter.getName( "USER" ) + ":" ), c );
            c.gridx = 1;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            databasePanel.add( getDatabaseUserField(), c );
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 1;
            databasePanel.add( new JLabel( DataImporter.getName( "PASSWORD" ) + ":" ), c );
            c.gridx = 1;
            c.gridy = 2;
            c.gridwidth = 2;
            databasePanel.add( getDatabasePasswordField(), c );
            c.gridx = 0;
            c.gridy = 3;
            c.gridwidth = 1;
            databasePanel.add( new JLabel( DataImporter.getName( "DATABASE" ) + ":" ), c );
            c.gridx = 1;
            c.gridy = 3;
            c.gridwidth = 2;
            databasePanel.add( getDatabaseField(), c );
            c.gridx = 0;
            c.gridy = 4;
            c.gridwidth = 1;
            databasePanel.add( new JLabel( DataImporter.getName( "CLEAR_DATABASE" ) + ":" ), c );
            c.gridx = 1;
            c.gridy = 4;
            databasePanel.add( getClearDatabaseField(), c );
            c.gridx = 2;
            c.gridy = 4;
            c.weighty = 1;
            databasePanel.add( new JLabel( "<html>" + DataImporter.getName( "CLEAR_DATABASE_EXPLANATION" ) + "</html>" ), c );
        }
        return databasePanel;
    }

    private JTextField getDatabasePasswordField() {
        if (databasePasswordField == null) {
            databasePasswordField = new JTextField( state.getPassword() );
        }

        return databasePasswordField;
    }

    private JTextField getDatabaseServerField() {
        if (databaseServerField == null) {
            databaseServerField = new JTextField( state.getServer() );
        }

        return databaseServerField;
    }

    private JTextField getDatabaseUserField() {
        if (databaseUserField == null) {
            databaseUserField = new JTextField( state.getUser() );
        }

        return databaseUserField;
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton( DataImporter.getName( "SAVE" ) );
            okButton.addActionListener( l -> {
                updateState();
                DataImporter.saveProperties( state );

                OptionDialog.this.dispose();
            } );
        }
        return okButton;
    }

    private void updateState() {
        state.setDatabase( getDatabaseField().getText().trim() );
        state.setUser( getDatabaseUserField().getText().trim() );
        state.setPassword( getDatabasePasswordField().getText().trim() );
        state.setServer( getDatabaseServerField().getText().trim() );
        state.setClearDatabase( getClearDatabaseField().isSelected() );

    }
}
