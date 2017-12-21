package client.commonClasses;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import client.ServiceLocator;

public class Translator {
	private ServiceLocator sl = ServiceLocator.getServiceLocator();

	protected Locale currentLocale;
	private ResourceBundle resourceBundle;

	public Translator(String localeString) {
		// Can we find the language in our supported locales?
		// If not, use VM default locale
		Locale locale = Locale.getDefault();
		if (localeString != null) {
			Locale[] availableLocales = sl.getLocales();
			for (int i = 0; i < availableLocales.length; i++) {
				String tmpLang = availableLocales[i].getLanguage();
				if (localeString.substring(0, tmpLang.length()).equals(tmpLang)) {
					locale = availableLocales[i];
					break;
				}
			}
		}

		// Load the resource strings
		try {
			// source: stackoverflow.com/questions/1172424
			String dir = System.getProperty("user.dir").replace("\\", "/");
			File f = new File(dir + "/");
			URL[] urls = { f.toURI().toURL() };
			ClassLoader cl = new URLClassLoader(urls);
			resourceBundle = ResourceBundle.getBundle(sl.getAPP_NAME(), locale, cl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Locale.setDefault(locale); // Change VM default (for dialogs, etc.)
		currentLocale = locale;

	}

	/**
	 * Return the current locale; this is useful for formatters, etc.
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * Public method to get string resources, default to "--" *
	 */
	public String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "--";
		}
	}
}
