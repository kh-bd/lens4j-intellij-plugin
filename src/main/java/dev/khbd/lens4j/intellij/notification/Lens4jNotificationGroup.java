package dev.khbd.lens4j.intellij.notification;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import lombok.experimental.UtilityClass;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class Lens4jNotificationGroup {

    /**
     * Get lens4j notification group instance.
     *
     * @return lens4j notification group
     */
    public static NotificationGroup getInstance() {
        return NotificationGroupManager
                .getInstance()
                .getNotificationGroup("Lens4j plugin");
    }
}
