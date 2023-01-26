package dev.khbd.lens4j.intellij.common.version;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class JreSpecificVersionTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void parse_argumentIsNull_throwNPE() {
        JreSpecificVersion.parseOptional(null);
    }

    @Test
    public void parse_structureIsCorrect_returnParsedVersion() {
        Optional<JreSpecificVersion> version = JreSpecificVersion.parseOptional("0.1.3_jre11");

        assertThat(version).hasValue(new JreSpecificVersion(0, 1, 3, "11"));
    }

    @Test(dataProvider = "wrongStructureDataProvider")
    public void parse_structureIsNotCorrect_throwIllegalException(String versionStr) {
        Optional<JreSpecificVersion> version = JreSpecificVersion.parseOptional(versionStr);

        assertThat(version).isEmpty();
    }

    @DataProvider
    public static Object[][] wrongStructureDataProvider() {
        return new Object[][]{
                {"10"},
                {"0.1"},
                {"0.1.2.3"},
                {"0.1.2.3.5"},
                {"0.1.3_jdk14"}
        };
    }
}