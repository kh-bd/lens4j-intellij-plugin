package dev.khbd.lens4j.intellij.common.path;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class PathTest {

    private final PathParser pathParser = new PathParser();

    @Test
    public void getCorrectPrefix_pathIsValid_returnTheSame() {
        Path path = pathParser.parse("pr1.pr2.pr3");

        Path result = path.getCorrectPathPrefix();

        assertThat(result).isEqualTo(path);
    }

    @Test
    public void getCorrectPrefix_pathContainsSeveralPoints_returnPrefix() {
        Path path = pathParser.parse("pr1.pr2...pr3");

        Path result = path.getCorrectPathPrefix();

        assertThat(result).isEqualTo(pathParser.parse("pr1.pr2"));
    }

    @Test
    public void getCorrectPrefix_pathStartsFromPoint_returnEmpty() {
        Path path = pathParser.parse(".pr1.pr2");

        Path result = path.getCorrectPathPrefix();

        assertThat(result).isEqualTo(new Path());
    }

    @Test
    public void getCorrectPrefix_pathEndsWithPoint_returnWholeExceptLastPoint() {
        Path path = pathParser.parse("pr1.pr2.");

        Path result = path.getCorrectPathPrefix();

        assertThat(result).isEqualTo(pathParser.parse("pr1.pr2"));
    }
}