package de.joglearth.view;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


public interface Tessellator {
	Mesh tessellate(Tile tile, int subdivisions);
}
