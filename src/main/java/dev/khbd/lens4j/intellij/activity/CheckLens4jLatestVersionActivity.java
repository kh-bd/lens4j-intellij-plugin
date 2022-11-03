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
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class CheckLens4jLatestVersionActivity implements StartupActivity.DumbAware {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(.*:)([\\d.]+)(.*)");

    @Override
    public void runActivity(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        Map<Version, List<Module>> groupedByVersion =
                Arrays.stream(moduleManager.getModules())
                        .map(module -> new ModuleAndLens4j(module, findLens4jVersion(module)))
                        .filter(ModuleAndLens4j::isPresentStaleVersion)
                        .collect(Collectors.groupingBy(ModuleAndLens4j::getVersion, Collectors.mapping(ModuleAndLens4j::getModule, Collectors.toList())));

        groupedByVersion.forEach((version, modules) -> warnAboutStaleVersion(project, modules, version));
    }

    private void warnAboutStaleVersion(Project project, List<Module> modules, Version version) {
        List<String> names = modules.stream().map(Module::getName).collect(Collectors.toList());
        Notification notification = Lens4jNotificationGroup
                .getInstance()
                .createNotification(
                        Lens4jBundle.getMessage("activity.check.version.title"),
                        Lens4jBundle.getMessage("activity.check.version.message",
                                project.getName(),
                                names,
                                version,
                                Version.LATEST
                        ),
                        NotificationType.WARNING,
                        NotificationListener.URL_OPENING_LISTENER
                );
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
            return Optional.of(Version.parse(matcher.group(2)));
        }
        return Optional.empty();
    }

    @Value
    private static class ModuleAndLens4j {
        Module module;
        Version version;

        boolean isPresentStaleVersion() {
            return Objects.nonNull(version) && version.compareTo(Version.LATEST) < 0;
        }

    }

}
