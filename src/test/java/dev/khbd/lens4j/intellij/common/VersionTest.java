package dev.khbd.lens4j.intellij.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class VersionTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void parse_argumentIsNull_throwNPE() {
        Version.parse(null);
    }

    @Test
    public void parse_structureIsCorrect_returnParsedVersion() {
        Version version = Version.parse("0.1.3");

        assertThat(version).isEqualTo(new Version(0, 1, 3));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            dataProvider = "wrongStructureDataProvider",
            expectedExceptionsMessageRegExp = "Version has wrong structure\\. Expected structure is 'major\\.minor\\.build'")
    public void parse_structureIsNotCorrect_throwIllegalException(String version) {
        Version.parse(version);
    }

    @DataProvider
    public static Object[][] wrongStructureDataProvider() {
        return new Object[][]{
                {"10"},
                {"0.1"},
                {"0.1.2.3"},
                {"0.1.2.3.5"}
        };
    }
}