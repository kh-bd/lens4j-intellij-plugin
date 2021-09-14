package dev.khbd.lens4j.intellij;

import com.intellij.AbstractBundle;
import com.intellij.reference.SoftReference;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

/**
 * Message bundle for lens4j plugin.
 */
public class Lens4jBundle {

    private static final String PATH_TO_BUNDLE = "dev.khbd.lens4j.intellij.messages.Lens4jBundle";

    private static Reference<ResourceBundle> bundle;

    private Lens4jBundle() {
    }

    /**
     * Get message.
     *
     * @param key    message key
     * @param params message parameters
     * @return composed message
     */
    public static String getMessage(String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(Lens4jBundle.bundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            Lens4jBundle.bundle = new SoftReference<>(bundle);
        }
        return bundle;
    }
}
