package de.lighti.components.player.statistics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.lighti.io.DataImporter;
import de.lighti.io.ImageCache;
import de.lighti.model.game.Ability;

/**
 * SkillTree component displays a hero build. It creates a column with a header icon
 * for each different ability of a hero and then renders rows with markers
 * representing the order the player has upgraded the respective skill.
 * The colour scheme is loosely based opn Steam's default coulor scheme for hero builds.
 * @author Tobias Mahlmann
 *
 */
public class SkillTreecomponent extends JScrollPane {
    /**
     * Delegate component that takes care of the actual rendering. The outer component is just neded
     * for layouting.
     *
     * @author Tobias Mahlmann
     *
     */
    private class SkillPanel extends JPanel {
        /**
         * Generated id.
         */
        private static final long serialVersionUID = -9070781920194030418L;
        private List<Ability> abilities;
        private TreeMap<Long, String> abilityLog;

        private final static int INDENT = 5;

        private final int ICON_SIZE = 64;
        private final int MARKER_SIZE = 32;
        private final Color BACKGROUND_COLOR = new Color( 46, 47, 49 ); //Light gray
        private final Color ACTIVE_MARKER_COLOR = new Color( 120, 36, 27 ); //Dark red
        private final Color INACTIVE_MARKER_COLOR = new Color( 34, 34, 34 ); //Dark gray

        private SkillPanel() {
            super();

            setBackground( BACKGROUND_COLOR );
            setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        }

        @Override
        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );

            if (abilities != null && !abilities.isEmpty()) {
                final FontMetrics metrics = getFontMetrics( getFont() );
                final Map<String, Point> columns = new HashMap<String, Point>();
                int x = INDENT;
                int y = INDENT;
                for (final Ability a : abilities) {
                    if (a != null) {
                        columns.put( a.getKey(), new Point( x, y ) );

                        try {
                            final BufferedImage image = ImageCache.getAbilityImage( a.getKey() );
                            if (image == null) {
                                g.drawString( DataImporter.getName( a.getKey() ), x, y );
                                x += metrics.stringWidth( DataImporter.getName( a.getKey() ) ) + INDENT;
                            }
                            else {
                                g.drawImage( image, x, y, ICON_SIZE, ICON_SIZE, this );
                                x += ICON_SIZE + INDENT;
                            }
                        }

                        catch (final IOException e) {
                            LOGGER.warning( "Error loading image: " + e.getLocalizedMessage() );
                            g.drawString( DataImporter.getName( a.getKey() ), x, y );
                            x += metrics.stringWidth( DataImporter.getName( a.getKey() ) ) + INDENT;
                        }
                    }
                    else {
                        g.drawString( DataImporter.getName( "UNKNOWN_ABILITY" ), x, y );
                        x += metrics.stringWidth( DataImporter.getName( "UNKNOWN_ABILITY" ) ) + INDENT;
                    }
                }

                y += MARKER_SIZE / 2 + ICON_SIZE + 3 * INDENT;
                int i = 1;
                for (final String s : abilityLog.values()) {
                    for (final Map.Entry<String, Point> e : columns.entrySet()) {
                        if (e.getKey().equals( s )) {
                            //Draw active marker
                            x = (int) e.getValue().getX() + MARKER_SIZE + INDENT;
                            g.setColor( ACTIVE_MARKER_COLOR );
                            g.fillRect( x - MARKER_SIZE / 2, y - MARKER_SIZE / 2, MARKER_SIZE, MARKER_SIZE );
                            g.setColor( Color.WHITE );
                            final String number = Integer.toString( i );
                            final Rectangle2D bounds = metrics.getStringBounds( number, g );
                            g.drawString( number, x - (int) bounds.getWidth() / 2, y + (int) bounds.getHeight() / 2 );
                        }
                        else {
                            //Draw inactive marker
                            x = (int) e.getValue().getX() + MARKER_SIZE + INDENT;
                            g.setColor( INACTIVE_MARKER_COLOR );
                            g.fillRect( x - MARKER_SIZE / 2, y - MARKER_SIZE / 2, MARKER_SIZE, MARKER_SIZE );
                        }
                    }
                    y += MARKER_SIZE + INDENT;
                    i++;
                }
            }
        }

        /**
         * Recalculate the size of the inner component based on the number of different abilities and
         * how many level ups the hero had.
         */
        private void recalculateBounds() {
            if (abilities != null && !abilities.isEmpty()) {
                int x = INDENT;
                int y = INDENT + ICON_SIZE + 3 * INDENT;

                //We never change the font during rendering, so we can use the component's metrics to calculate the bounds
                final FontMetrics metrics = getFontMetrics( getFont() );

                for (final Ability a : abilities) {
                    if (a != null) {

                        try {
                            final BufferedImage image = ImageCache.getAbilityImage( a.getKey() );
                            if (image == null) {
                                x += metrics.stringWidth( DataImporter.getName( a.getKey() ) ) + INDENT;
                            }
                            else {
                                x += ICON_SIZE + INDENT;
                            }
                        }

                        catch (final IOException e) {
                            LOGGER.warning( "Error loading image: " + e.getLocalizedMessage() );
                            x += metrics.stringWidth( DataImporter.getName( a.getKey() ) ) + INDENT;
                        }
                    }
                    else {

                        x += metrics.stringWidth( DataImporter.getName( "UNKNOWN_ABILITY" ) ) + INDENT;
                    }
                }

                y += (MARKER_SIZE + INDENT) * abilityLog.size() + INDENT;

                setPreferredSize( new Dimension( x, y ) );
                setSize( new Dimension( x, y ) );
            }
        }

        private void setAbilities( List<Ability> abilities ) {
            if (abilities != this.abilities) {
                this.abilities = abilities;

                abilityLog = new TreeMap<Long, String>();
                for (final Ability a : abilities) {
                    if (a != null) {
                        for (final Long l : a.getLevel().keySet()) {
                            if (a.getLevel().get( l ) > 0) {
                                abilityLog.put( l, a.getKey() );
                            }
                        }
                    }
                }
                recalculateBounds();
            }
        }
    }

    /**
     * Generated id.
     */
    private static final long serialVersionUID = -5620172809015835852L;

    private final static Logger LOGGER = Logger.getLogger( SkillTreecomponent.class.getName() );
    private final SkillPanel skillPanel;

    /**
     * Default constructor.
     */
    public SkillTreecomponent() {
        super();

        final JPanel filler = new JPanel();
        filler.setLayout( new FlowLayout() );
        skillPanel = new SkillPanel();
        filler.add( skillPanel );
        setBorder( null );
        setViewportView( filler );

    }

    /**
     * Main update method for this component.
     * @param abilities a ordered list of a hero abilities.
     */
    public void setAbilities( List<Ability> abilities ) {
        skillPanel.setAbilities( abilities );
    }

}
