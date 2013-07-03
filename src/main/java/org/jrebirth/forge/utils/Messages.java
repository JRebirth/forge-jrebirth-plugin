package org.jrebirth.forge.utils;

import java.util.ResourceBundle;

public final class Messages {

    public static final Messages INSTANCE = new Messages();

    private final ResourceBundle bundle;

    private Messages() {
        bundle = ResourceBundle.getBundle("ResourceBundle");
    }

    public String getKeyValue(final String key) {
        return bundle.getString(key);
    }
    
    public String getMessage(final String key) {
        return bundle.getString("message."+key);
    }

    public String getMessage(final String key, final Object... args) {
        return String.format(bundle.getString("message."+key), args);

    }
}