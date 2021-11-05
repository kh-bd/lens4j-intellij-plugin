package dev.khbd.lens4j.intellij.common;

import lombok.NonNull;
import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class Version implements Comparable<Version> {

    public static final Version LATEST = Version.parse("0.1.4");

    int major;
    int minor;
    int build;

    @Override
    public int compareTo(@NonNull Version other) {
        return toInt(this) - toInt(other);
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

    private static int toInt(Version version) {
        return 100 * version.major + 10 * version.minor + version.build;
    }

}
