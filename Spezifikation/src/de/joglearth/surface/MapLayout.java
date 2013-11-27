package de.joglearth.surface;

/**
 * Enumerates types of map texture layouts handled by a {@link de.joglearth.rendering.Renderer}.
 */
public enum MapLayout {

    /**
     * The map is a single texture covering the whole earth. A single sphere or plane must be
     * rendered to display it.
     */
    SINGLE,

    /**
     * The map is splitted into power-of-two subdivisions along both axes and exists in multiple
     * zoom levels as defined by OpenStreetMap.
     */
    TILED
}
