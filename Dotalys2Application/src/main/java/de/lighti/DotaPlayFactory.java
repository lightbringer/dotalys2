package de.lighti;

/**
 * This class has no real use at the moment. it was used to switch between Skadi's clarity and our old built-in parser
 * @author tmahl
 *
 */
public class DotaPlayFactory {
    public static DotaPlay getInstance() {
        return new SkadiDotaPlay();
    }

}
