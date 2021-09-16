package dev.khbd.lens4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class PathParserTest {

    private final PathParser pathParser = new PathParser();

    @Test
    public void parse_pathDoNotContainPoints_wholePathIsProperty() {
        Path path = pathParser.parse("property");

        assertThat(path).containsExactly(new PathPart("property", 0, 7));
    }

    @Test
    public void parse_pathContainTwoProperties_returnExpectedChunks() {
        Path path = pathParser.parse("property1.property2");

        assertThat(path).containsExactly(
                new PathPart("property1", 0, 8),
                new PathPart("property2", 10, 18)
        );
    }

    @Test
    public void parse_pathSeveralProperties_returnExpectedChunks() {
        Path path = pathParser.parse("property1.property2.property3.property4");

        assertThat(path).containsExactly(
                new PathPart("property1", 0, 8),
                new PathPart("property2", 10, 18),
                new PathPart("property3", 20, 28),
                new PathPart("property4", 30, 38)
        );
    }
}