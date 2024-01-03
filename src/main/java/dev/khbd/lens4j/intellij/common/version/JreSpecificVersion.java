package dev.khbd.lens4j.intellij.common.version;

import static dev.khbd.interp4j.core.Interpolations.s;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public record JreSpecificVersion(int major, int minor, int build, String jreVersion) implements Version {

    private static final List<JreSpecificVersion> LATEST = List.of(
            JreSpecificVersion.parse("1.0.0_jre1.8"),
            JreSpecificVersion.parse("1.0.0_jre11"),
            JreSpecificVersion.parse("1.0.0_jre17"),
            JreSpecificVersion.parse("1.0.0_jre21")
    );

    @Override
    public boolean isLatest() {
        return LATEST.contains(this);
    }

    @Override
    public String toString() {
        return s("${major}.${minor}.${build}_jre${jreVersion}");
    }

    /**
     * Parse string into version instance if it has valid structure.
     *
     * @param version string representation of version
     * @return version instance
     */
    static Optional<JreSpecificVersion> parseOptional(@NonNull String version) {
        String[] parts = version.split("_jre");
        if (parts.length != 2) {
            return Optional.empty();
        }
        return GenericVersion.parseOptional(parts[0])
                .map(gv -> gv.withJreVersion(parts[1]));
    }

    private static JreSpecificVersion parse(String version) {
        return parseOptional(version)
                .orElseThrow(() -> new IllegalArgumentException("Version has wrong structure. Expected structure is '[major].[minor].[build]_jre[jre_version]'"));
    }
}
