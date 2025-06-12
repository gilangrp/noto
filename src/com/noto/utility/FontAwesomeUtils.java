package com.noto.utility;
import java.awt.Font;
import java.io.InputStream;

/**
 * Utility class for loading and using FontAwesome icons
 */
public abstract class FontAwesomeUtils {
    private static Font fontAwesome = null;
    
    /**
     * Get the FontAwesome font at the specified size
     * @param size Font size
     * @return FontAwesome font
     */
    public static Font getFont(float size) {
        if (fontAwesome == null) {
            try {
                // Load FontAwesome font from resources
                InputStream is = FontAwesomeUtils.class.getResourceAsStream("/resources/fontawesome-webfont.ttf");
                fontAwesome = Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (Exception e) {
                System.err.println("Error loading FontAwesome font: " + e.getMessage());
                // Fallback to a default font
                return new Font("SansSerif", Font.PLAIN, (int)size);
            }
        }
        return fontAwesome.deriveFont(size);
    }
    
    // FontAwesome icon unicode constants
    public static final String ICON_USER = "\uf007";
    public static final String ICON_LOCK = "\uf023";
    public static final String ICON_ENVELOPE = "\uf0e0";
    public static final String ICON_CHECK = "\uf00c";
    public static final String ICON_TIMES = "\uf00d";
    public static final String ICON_EXCLAMATION = "\uf12a";
    public static final String ICON_INFO = "\uf129";
    public static final String ICON_ARROW_RIGHT = "\uf061";
}
