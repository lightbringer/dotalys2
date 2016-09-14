package de.lighti.components;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class ProgressDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 2666391304959459393L;
    private JProgressBar progressBar;

    public ProgressDialog( Dialog owner ) {
        super( owner );

        setModalityType( ModalityType.APPLICATION_MODAL );
        setUndecorated( true );
        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        add( getProgressBar() );
        setLocation( owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 );
        pack();
    }

    public ProgressDialog( Frame owner ) {
        super( owner );

        setModalityType( ModalityType.APPLICATION_MODAL );
        setUndecorated( true );
        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        add( getProgressBar() );
        setLocation( owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 );
        pack();
    }

    public JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar();
        }
        return progressBar;
    }

    public void setMaximum( long fs ) {
        getProgressBar().setMaximum( (int) fs );

    }

    public void setValue( long value ) {
        getProgressBar().setValue( (int) value );
    }
}
