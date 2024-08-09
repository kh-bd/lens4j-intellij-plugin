package dev.khbd.lens4j.intellij.common.version;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class GenericVersionTest {

    @Test
    public void parse_argumentIsNull_throwNPE() {
        Throwable error = Assertions.catchThrowable(() -> GenericVersion.parseOptional(null));

        assertThat(error).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void parse_structureIsCorrect_returnParsedVersion() {
        Optional<GenericVersion> version = GenericVersion.parseOptional("0.1.3");

        assertThat(version).hasValue(new GenericVersion(0, 1, 3));
    }

    @ParameterizedTest
    @MethodSource("wrongStructureDataProvider")
    public void parse_structureIsNotCorrect_throwIllegalException(String versionStr) {
        Optional<GenericVersion> version = GenericVersion.parseOptional(versionStr);

        assertThat(version).isEmpty();
    }

    public static List<String> wrongStructureDataProvider() {
        return List.of(
                "10",
                "0.1",
                "0.1.2.3",
                "0.1.2.3.5"
        );
    }
}