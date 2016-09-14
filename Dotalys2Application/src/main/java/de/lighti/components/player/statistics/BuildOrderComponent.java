package de.lighti.components.player.statistics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Queue;
import java.util.logging.Logger;

import javax.swing.JPanel;

import de.lighti.io.ImageCache;
import de.lighti.model.game.Dota2Item;

public class BuildOrderComponent extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 6036192244830446704L;

    private Queue<Dota2Item> items;

    private final static Logger LOGGER = Logger.getLogger( BuildOrderComponent.class.getName() );

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        int x = 20;
        int y = 20;
        if (items != null) {
            for (final Dota2Item i : items) {
                try {
                    final BufferedImage image = ImageCache.getItemImage( i.getKey() );
                    if (image != null) {
                        g.drawImage( image, x, y, null );
                        x += image.getWidth() + 5;
                    }
                    else {
                        g.drawString( i.getKey(), x, y );
                        x += i.getKey().length() * 20;
                    }
                }
                catch (final IOException e) {
                    LOGGER.warning( "Error loading image: " + e.getLocalizedMessage() );
                    g.drawString( i.getKey(), x, y );
                    x += i.getKey().length() * 20;
                }

                if (x >= getWidth() - 100) {
                    y += 100;
                    x = 20;
                }
            }
        }
    }

    public void setItems( Queue<Dota2Item> items ) {
        if (items != this.items) {
            this.items = items;
            repaint();
        }
    }

}
