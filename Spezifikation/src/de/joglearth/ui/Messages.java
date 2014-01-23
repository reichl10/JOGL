package de.joglearth.ui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Used to load translations.
 */
public class Messages {
    
	private static final String BUNDLE_NAME = "de.joglearth.ui.messages"; //$NON-NLS-1$
	private static Locale locale;

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Used to load the translation of a given key.
	 * 
	 * @param key The key to translate
	 * @return The translation
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Set the {@link Locale} to use.
	 * 
	 * @param l The Locale
	 */
	public static void setLocale(Locale l) {
		locale = l;
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}
}
