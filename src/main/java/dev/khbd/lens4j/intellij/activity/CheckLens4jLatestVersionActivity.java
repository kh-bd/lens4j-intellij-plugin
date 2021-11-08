package dev.khbd.lens4j.intellij.activity;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.startup.StartupActivity;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.Version;
import dev.khbd.lens4j.intellij.notification.Lens4jNotificationGroup;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergei_Khadanovich
 */
public class CheckLens4jLatestVersionActivity implements StartupActivity.DumbAware {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(.*:)([\\d.]+)(.*)");

    @Override
    public void runActivity(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            OrderEntry entry = findLens4jEntry(module);
            if (Objects.nonNull(entry)) {
                Matcher matcher = VERSION_PATTERN.matcher(entry.getPresentableName());
                if (matcher.matches()) {
                    checkVersion(project, module, Version.parse(matcher.group(2)));
                }
            }
        }
    }

    private void checkVersion(Project project, Module module, Version version) {
        if (version.compareTo(Version.LATEST) < 0) {
            Notification notification = Lens4jNotificationGroup
                    .getInstance()
                    .createNotification(
                            Lens4jBundle.getMessage("activity.check.version.title"),
                            Lens4jBundle.getMessage("activity.check.version.message",
                                    project.getName(),
                                    module.getName(),
                                    version,
                                    Version.LATEST
                            ),
                            NotificationType.WARNING,
                            NotificationListener.URL_OPENING_LISTENER
                    );
            Notifications.Bus.notify(notification, project);
        }
    }

    private OrderEntry findLens4jEntry(Module module) {
        ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        for (OrderEntry entry : rootManager.getOrderEntries()) {
            if (entry.getPresentableName().contains("lens4j")) {
                return entry;
            }
        }
        return null;
    }

}
