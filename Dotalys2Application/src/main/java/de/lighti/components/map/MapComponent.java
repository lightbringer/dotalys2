package de.lighti.components.map;

import de.lighti.components.map.data.Dota2MapModel;

public interface MapComponent {

    MapCanvasComponent getMapCanvas();

    Dota2MapModel getPlaybackScript();

}
