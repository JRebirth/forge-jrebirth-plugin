package org.jrebirth.forge.completer;

import java.util.ArrayList;

import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * The Class ColorTypeCompleter.
 */
public class ColorTypeCompleter extends SimpleTokenCompleter {

    /** The color type. */
    public static ArrayList<String> COLOR_TYPE = new ArrayList<>();

    static {

        COLOR_TYPE.add("web");
        COLOR_TYPE.add("gray");
        COLOR_TYPE.add("hsb");
        COLOR_TYPE.add("rgb01");
        COLOR_TYPE.add("rgb255");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<?> getCompletionTokens() {

        return COLOR_TYPE;
    }
    
    /**
     * Checks if is color type exist.
     * 
     * @param colorType the color type
     * @return true, if is color type exist
     */
    public static boolean isColorTypeExist(String colorType) {
        return COLOR_TYPE.contains(colorType);
    }

}
