package de.joglearth.rendering;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.surface.HeightMapManager;
import de.joglearth.ui.*;


public interface Tessellator {
	Mesh tessellateTile(Tile tile, int subdivisions, HeightMapManager heightMap);
}
