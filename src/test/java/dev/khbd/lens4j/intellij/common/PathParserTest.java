package dev.khbd.lens4j.intellij.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class PathParserTest {

    private final PathParser pathParser = new PathParser();

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "path should be not blank")
    public void parse_pathIsBlank_throwError() {
        pathParser.rawParse("  ");
    }

    @Test
    public void parse_pathDoNotContainPoints_wholePathIsProperty() {
        RawPath path = pathParser.rawParse("property");

        assertThat(path).containsExactly(new RawPathElement("property", 0, 7));
    }

    @Test
    public void parse_pathContainTwoProperties_returnExpectedChunks() {
        RawPath path = pathParser.rawParse("property1.property2");

        assertThat(path).containsExactly(
                new RawPathElement("property1", 0, 8),
                new RawPathElement("property2", 10, 18)
        );
    }

    @Test
    public void parse_pathSeveralProperties_returnExpectedChunks() {
        RawPath path = pathParser.rawParse("property1.property2.property3.property4");

        assertThat(path).containsExactly(
                new RawPathElement("property1", 0, 8),
                new RawPathElement("property2", 10, 18),
                new RawPathElement("property3", 20, 28),
                new RawPathElement("property4", 30, 38)
        );
    }
}