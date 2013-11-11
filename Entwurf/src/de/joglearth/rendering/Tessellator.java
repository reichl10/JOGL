package de.joglearth.rendering;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


public interface Tessellator {
	Mesh tessellate(Tile tile, int subdivisions);
}
