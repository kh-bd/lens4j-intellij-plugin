package dev.khbd.lens4j.intellij.common.version;

import static dev.khbd.interp4j.core.Interpolations.s;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Class to represent old versioning schema.
 *
 * @author Sergei_Khadanovich
 */
public record GenericVersion(int major, int minor, int build) implements Version {

    private static final List<GenericVersion> LATEST = List.of(
            GenericVersion.parse("0.1.9"),
            GenericVersion.parse("0.2.1")
    );

    @Override
    public boolean isLatest() {
        return LATEST.contains(this);
    }

    @Override
    public String toString() {
        return s("${major}.${minor}.${build}");
    }

    /**
     * Create jre specific version from generic one.
     *
     * @param jreVersion jre version
     */
    public JreSpecificVersion withJreVersion(@NonNull String jreVersion) {
        return new JreSpecificVersion(major, minor, build, jreVersion);
    }

    /**
     * Parse version if it has correct structure.
     *
     * @param version version as string
     */
    static Optional<GenericVersion> parseOptional(@NonNull String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }
        try {
            GenericVersion result = new GenericVersion(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
            return Optional.of(result);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    private static GenericVersion parse(String version) {
        return parseOptional(version)
                .orElseThrow(() -> new IllegalArgumentException("Version has wrong structure. Expected structure is 'major.minor.build'"));
    }

}
