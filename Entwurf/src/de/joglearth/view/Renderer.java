package de.joglearth.view;

public class Renderer {
	
	public Renderer() {
		
	}

	// Benachrichtigt den Renderer, dass mindestens ein Frame gerendert 
	// werden muss. Wenn vorher start() aufgerufen wurde, hat die 
	// Methode u.U. keine Auswirkung. Asynchron, wartet nicht bis der
	// Frame gezeichnet wurde.
	public void post() {
		
	}

	// Beginnt mit einer konstanten FPS-Zahl zu rendern, zB. 60.
	// Asynchron, kehrt sofort zurück.
	public void start() {
		
	}
	
	// Beendet eine Renderschleife, die mit start() angestoßen wurde.
	// u.U. wird trotzdem noch ein Frame gerendert, falls derweil
	// post() aufgerufen wurde.
	public void stop() {
		
	}

}
