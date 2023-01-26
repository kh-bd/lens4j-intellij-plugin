package dev.khbd.lens4j.intellij.common.version;

import lombok.NonNull;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public sealed interface Version permits GenericVersion, JreSpecificVersion {

    /**
     * Check if version is latest or not.
     */
    boolean isLatest();

    /**
     * Parse version.
     */
    static Optional<Version> parseOptional(@NonNull String version) {
        return JreSpecificVersion.parseOptional(version)
                .map(v -> (Version) v)
                .or(() -> GenericVersion.parseOptional(version));
    }

}
