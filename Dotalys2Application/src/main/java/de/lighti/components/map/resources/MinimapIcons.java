package de.lighti.components.map.resources;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class MinimapIcons {
    public enum ICON_FLAVOUR {
        NORMAL, DEAD, STUNNED
    }

    private static BufferedImage roshanImage;

    private static Map<String, BufferedImage> CACHE = new HashMap<String, BufferedImage>();

    private final static int ICON_HEIGHT = 32;

    private final static int ICON_WIDTH = 32;;

    private final static Logger LOGGER = Logger.getLogger( MinimapIcons.class.getName() );

    private final static String MINIMAP_ICONS_FILE = "minimap_hero_sheet.png";

    private static final String MINIMAP_TOWER45_FILE = "minimap_tower45.png";

    private static final String MINIMAP_TOWER90_FILE = "minimap_tower90.png";

    private static final String MINIMAP_ROSHAN_FILE = "minimap_roshancamp.png";
    private static BufferedImage textureAtlas;

    private static BufferedImage towerImage45;
    private static BufferedImage towerImage90;

    static {

        try {
            textureAtlas = ImageIO.read( MinimapIcons.class.getResource( MINIMAP_ICONS_FILE ) );
            towerImage45 = ImageIO.read( MinimapIcons.class.getResource( MINIMAP_TOWER45_FILE ) );
            towerImage90 = ImageIO.read( MinimapIcons.class.getResource( MINIMAP_TOWER90_FILE ) );
            roshanImage = ImageIO.read( MinimapIcons.class.getResource( MINIMAP_ROSHAN_FILE ) );
        }
        catch (final IOException e) {
            Logger.getLogger( MinimapIcons.class.getName() ).severe( e.getMessage() );
        }
    }

    public static Image getMinimapIcon( String unitName, boolean isRadiant, ICON_FLAVOUR flavour ) {
        if (CACHE.containsKey( unitName + flavour )) {
            return CACHE.get( unitName + flavour );
        }
        BufferedImage i = null;
        //Hero
        if (unitName.startsWith( "CDOTA_UNIT_HERO" )) {
            i = getHeroImage( unitName, isRadiant, flavour );
        }
        //Roshan
        if (unitName.equals( "CDOTA_UNIT_ROSHAN" )) {
            i = getRoshanImage( unitName, flavour );
        }
        //Tower
        else if (unitName.contains( "TOWER" )) {
            i = getTowerImage( unitName, flavour );
        }
        if (i == null) {
            LOGGER.warning( "No minimap icon for unit " + unitName );
        }
        else {
            CACHE.put( unitName + flavour, i );
        }
        return i;
    };

    private static void colorIcon( BufferedImage image, Color color, Color contour ) {
        final BufferedImage original = image;
        image = deepCopy( image );
        final int w = image.getWidth();
        final int h = image.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                final Color c = new Color( image.getRGB( i, j ), true );
                if (contour != null && i > 0 && i < w - 1 && j > 0 && j < h - 1) {
                    final boolean north = new Color( original.getRGB( i, j - 1 ), true ).getAlpha() == 255;
                    final boolean south = new Color( original.getRGB( i, j + 1 ), true ).getAlpha() == 255;
                    final boolean east = new Color( original.getRGB( i + 1, j ), true ).getAlpha() == 255;
                    final boolean west = new Color( original.getRGB( i - 1, j ), true ).getAlpha() == 255;

                    if (north ^ south || east ^ west) {
                        image.setRGB( i, j, contour.getRGB() );
                        continue;
                    }
                }
                if (color != null) {
                    int r, g, b, a;
                    r = (c.getRed() + color.getRed()) / 2;
                    g = (c.getGreen() + color.getGreen()) / 2;
                    b = (c.getBlue() + color.getBlue()) / 2;
                    a = c.getAlpha();

                    image.setRGB( i, j, new Color( r, g, b, a ).getRGB() );
                }
            }
        }
        original.setData( image.getData() );
    }

    private static BufferedImage deepCopy( BufferedImage bi ) {
        final ColorModel cm = bi.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = bi.copyData( bi.getRaster().createCompatibleWritableRaster() );
        return new BufferedImage( cm, raster, isAlphaPremultiplied, null );
    }

    private static int[] getHeroBase( String s ) {

        final String icon = "ICON_" + s.toUpperCase();
        try {
            final Field f = HeroIconAtlas.class.getDeclaredField( icon );
            return (int[]) f.get( null );
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.warning( "No minimap icon for hero " + s );
            return null;
        }

    }

    private static BufferedImage getHeroImage( String unitName, boolean isRadiant, ICON_FLAVOUR flavour ) {
        final int[] xy = getHeroBase( unitName );

        if (xy == null) {
            return null;
        }
        else {
            final BufferedImage i = deepCopy( textureAtlas.getSubimage( xy[0], xy[1], ICON_WIDTH, ICON_HEIGHT ) );
            final Color green = Color.GREEN.darker();
            final Color red = Color.RED.darker();
            Color statusColor;
            switch (flavour) {
                case DEAD:
                    statusColor = Color.GRAY;
                    break;
                case STUNNED:
                    statusColor = Color.BLUE;
                    break;
                default:
                    statusColor = null;
                    break;

            }

            colorIcon( i, statusColor, isRadiant ? green : red );

            return i;
        }
    }

    private static BufferedImage getRoshanImage( String unitName, ICON_FLAVOUR flavour ) {

        final BufferedImage i = deepCopy( roshanImage );

        if (flavour != ICON_FLAVOUR.DEAD) {
            colorIcon( i, Color.RED, null );
        }
        else {
            colorIcon( i, Color.GRAY, null );
        }

        return i;
    }

    private static BufferedImage getTowerImage( String unitName, ICON_FLAVOUR dead ) {
        BufferedImage i;
        if (unitName.contains( "MID" ) || unitName.contains( "4" )) {
            i = deepCopy( towerImage45 );

        }
        else {
            i = deepCopy( towerImage90 );
        }
        if (dead != ICON_FLAVOUR.DEAD) {
            if (unitName.contains( "GOOD" )) {
                colorIcon( i, Color.GREEN, null );
            }
            else {
                colorIcon( i, Color.RED, null );
            }
        }
        else {
            colorIcon( i, Color.GRAY, null );
        }

        return i;
    }

    private MinimapIcons() {

    }
}
