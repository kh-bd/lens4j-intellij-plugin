package dev.khbd.lens4j.intellij.common.path;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class PathServiceTest {

    private final PathParser pathParser = new PathParser();
    private final PathService service = new PathService();

    @Test
    public void getCorrectPrefix_pathIsValid_returnTheSame() {
        Path path = pathParser.parse("pr1.pr2.pr3");

        Path result = service.getCorrectPathPrefix(path);

        assertThat(result).isEqualTo(path);
    }

    @Test
    public void getCorrectPrefix_pathContainsSeveralPoints_returnPrefix() {
        Path path = pathParser.parse("pr1.pr2...pr3");

        Path result = service.getCorrectPathPrefix(path);

        assertThat(result).isEqualTo(pathParser.parse("pr1.pr2."));
    }

    @Test
    public void getCorrectPrefix_pathStartsFromPoint_returnEmpty() {
        Path path = pathParser.parse(".pr1.pr2");

        Path result = service.getCorrectPathPrefix(path);

        assertThat(result).isEqualTo(new Path());
    }

    @Test
    public void getCorrectPrefix_pathEndsWithPoint_returnWholeExceptLastPoint() {
        Path path = pathParser.parse("pr1.pr2.");

        Path result = service.getCorrectPathPrefix(path);

        assertThat(result).isEqualTo(pathParser.parse("pr1.pr2."));
    }

    // hasCorrectStructure

    @Test
    public void hasCorrectStructure_pathIsEmpty_returnTrue() {
        Path path = pathParser.parse("");

        assertThat(service.hasCorrectStructure(path)).isTrue();
    }

    @Test
    public void hasCorrectStructure_onlyPoint_returnFalse() {
        Path path = pathParser.parse(".");

        assertThat(service.hasCorrectStructure(path)).isFalse();
    }

    @Test
    public void hasCorrectStructure_startsFromPoint_returnFalse() {
        Path path = pathParser.parse(".pr");

        assertThat(service.hasCorrectStructure(path)).isFalse();
    }

    @Test
    public void hasCorrectStructure_severalPointsInTheMiddle_returnFalse() {
        Path path = pathParser.parse("pr1...pr2");

        assertThat(service.hasCorrectStructure(path)).isFalse();
    }

    @Test
    public void hasCorrectStructure_endsWithPoint_returnTrue() {
        Path path = pathParser.parse("pr1.pr2.");

        assertThat(service.hasCorrectStructure(path)).isTrue();
    }

    @Test
    public void hasCorrectStructure_severalProperties_returnTrue() {
        Path path = pathParser.parse("pr1.pr2.pr3.pr4");

        assertThat(service.hasCorrectStructure(path)).isTrue();
    }

}