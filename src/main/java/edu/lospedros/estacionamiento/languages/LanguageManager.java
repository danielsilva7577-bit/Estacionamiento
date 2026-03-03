package edu.lospedros.estacionamiento.languages;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Gestor de internacionalización (i18n) para la aplicación.
 * <p>
 * Carga y proporciona cadenas de texto localizadas desde archivos de propiedades.
 * </p>
 */
public class LanguageManager {
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    static {
        // Carga inicial
        setLocale("es");
    }

    /**
     * Cambia el idioma actual de la aplicación.
     *
     * @param lang Código del idioma (e.g., "es", "en").
     */
    public static void setLocale(String lang) {
        try {
            currentLocale = new Locale(lang);
            // ESTA RUTA DEBE SER IDÉNTICA A TUS CARPETAS EN 'RESOURCES'
            // Si tu carpeta en resources se llama 'languages', aquí debe decir 'languages'
            bundle = ResourceBundle.getBundle("edu.lospedros.estacionamiento.languages.messages", currentLocale);
        } catch (Exception e) {
            System.err.println("ERROR: No se encontró el archivo de idiomas en: edu.lospedros.estacionamiento.languages.messages");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una cadena localizada por su clave.
     *
     * @param key La clave del mensaje en el archivo de propiedades.
     * @return El texto localizado, o la clave entre signos de exclamación si no se encuentra.
     */
    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}
