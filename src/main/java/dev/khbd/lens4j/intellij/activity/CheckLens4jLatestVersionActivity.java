package dev.khbd.lens4j.intellij.activity;

import com.intellij.notification.BrowseNotificationAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.startup.StartupActivity;
import dev.khbd.lens4j.intellij.Lens4jBundle;
import dev.khbd.lens4j.intellij.common.version.Version;
import dev.khbd.lens4j.intellij.notification.Lens4jNotificationGroup;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergei_Khadanovich
 */
public class CheckLens4jLatestVersionActivity implements StartupActivity.DumbAware {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(.*:)(.+)");

    @Override
    public void runActivity(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        boolean hasStaleVersion =
                Arrays.stream(moduleManager.getModules())
                        .map(module -> new ModuleAndLens4j(module, findLens4jVersion(module)))
                        .anyMatch(ModuleAndLens4j::isPresentStaleVersion);

        if (hasStaleVersion) {
            warnAboutStaleVersion(project);
        }
    }

    private void warnAboutStaleVersion(Project project) {
        Notification notification = Lens4jNotificationGroup.getInstance()
                .createNotification(
                        Lens4jBundle.getMessage("activity.check.version.title"),
                        Lens4jBundle.getMessage("activity.check.version.message"),
                        NotificationType.WARNING
                );
        notification.addAction(new BrowseNotificationAction(
                Lens4jBundle.getMessage("activity.check.version.action"),
                Lens4jBundle.getMessage("activity.check.version.action.url")
        ));
        Notifications.Bus.notify(notification, project);
    }

    private Version findLens4jVersion(Module module) {
        ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        return Arrays.stream(rootManager.getOrderEntries())
                .map(OrderEntry::getPresentableName)
                .filter(name -> name.contains("lens4j"))
                .findFirst()
                .flatMap(this::parseVersion)
                .orElse(null);
    }

    private Optional<Version> parseVersion(String lens4jName) {
        Matcher matcher = VERSION_PATTERN.matcher(lens4jName);
        if (matcher.matches()) {
            return Version.parseOptional(matcher.group(2));
        }
        return Optional.empty();
    }

    private record ModuleAndLens4j(Module module, Version version) {

        boolean isPresentStaleVersion() {
            return Objects.nonNull(version) && !version.isLatest();
        }

    }

}
