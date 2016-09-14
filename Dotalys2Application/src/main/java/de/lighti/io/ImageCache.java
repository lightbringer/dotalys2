package de.lighti.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public final class ImageCache {
    private static BufferedImage fetchImage( String baseURL, String name ) {

        try {
            final URL url = new URL( baseURL.replace( "<name>", name ) );
            LOGGER.fine( "Trying to fetch " + url );
            return ImageIO.read( url );
        }
        catch (final IOException e) {
            LOGGER.warning( "Could not fetch image for " + name + ": " + e.getLocalizedMessage() );
            return null;
        }
    }

    public static BufferedImage getAbilityImage( String name ) throws IOException {
        return getImage( ABILITY_BASE_URL, name.toLowerCase() );
    }

    private static BufferedImage getImage( String baseURL, String id ) throws IOException {
        if (memoryCache.containsKey( id )) {
            //File is in memory
            return memoryCache.get( id );
        }
        else {
            final File cacheDir = new File( CACHE_DIR );
            if (System.getSecurityManager() == null && cacheDir.isDirectory()) {

                final String filename = CACHE_DIR + "/" + id + FILE_SUFFIX;
                final File f = new File( filename );
                if (f.exists()) {
                    //Read image from disc
                    final BufferedImage image = ImageIO.read( f );
                    memoryCache.put( id, image );
                    return image;
                }
                else {
                    //Try to fetch image from web
                    final BufferedImage image = fetchImage( baseURL, id );
                    if (image != null) {
                        ImageIO.write( image, "png", f );
                    }
                    memoryCache.put( id, image );
                    return image;
                }
            }
            else {
                //This is most likely an applet context. Try just to load it
                //Try to fetch image from web
                final BufferedImage image = fetchImage( baseURL, id );
                memoryCache.put( id, image );
                return image;
            }
        }
    }

    public static BufferedImage getItemImage( String id ) throws IOException {
        if (id.startsWith( "item_" )) {
            id = id.replace( "item_", "" );
        }
        return getImage( ITEM_BASE_URL, id );

    }

    private final static String FILE_SUFFIX = "_lg.png";

    private static final String ITEM_BASE_URL = "http://media.steampowered.com/apps/dota2/images/items/<name>" + FILE_SUFFIX;

    private final static String ABILITY_BASE_URL = "http://media.steampowered.com/apps/dota2/images/abilities/<name>" + FILE_SUFFIX;

    private static final String CACHE_DIR = "cache";

    private static Map<String, BufferedImage> memoryCache;

    static {
        memoryCache = new HashMap<String, BufferedImage>();
    }

    private final static Logger LOGGER = Logger.getLogger( ImageCache.class.getName() );
}
