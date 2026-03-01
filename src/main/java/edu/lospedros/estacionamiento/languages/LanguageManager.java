package edu.lospedros.estacionamiento.languages; // <--- Mira si tu carpeta termina en 's' o no

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager { // <--- Nombre con 'e'
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    static {
        // Carga inicial
        setLocale("es");
    }

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

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}