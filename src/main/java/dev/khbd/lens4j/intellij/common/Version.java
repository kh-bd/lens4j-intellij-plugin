package dev.khbd.lens4j.intellij.common;

import lombok.NonNull;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public record Version(int major, int minor, int build) {

    public static final List<Version> LATEST = List.of(
            Version.parse("0.1.9"),
            Version.parse("0.2.1")
    );

    /**
     * Check is version latest or not.
     *
     * @return {@literal true} if version is latest and {@literal false} otherwise
     */
    public boolean isLatest() {
        return LATEST.contains(this);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }

    /**
     * Parse string into version instance.
     *
     * @param version string representation of version
     * @return version instance
     */
    public static Version parse(@NonNull String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version has wrong structure. Expected structure is 'major.minor.build'");
        }
        return new Version(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

}
