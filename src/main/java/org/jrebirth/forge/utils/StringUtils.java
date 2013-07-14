package org.jrebirth.forge.utils;

/**
 * The class <strong>StringUtils</strong>.
 * 
 * @author Sébastien Bordes
 */
public class StringUtils {

    /**
     * Convert aStringUnderscored into A_STRING_UNDESCORED.
     * 
     * @param camelCaseString the string to convert
     * 
     * @return the underscored string
     */
    public static String camelCaseToUnderscore(final String camelCaseString) {

        StringBuilder sb = new StringBuilder();
        for (String camelPart : camelCaseString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(camelPart.toUpperCase());
        }
        return sb.toString();
    }
}
