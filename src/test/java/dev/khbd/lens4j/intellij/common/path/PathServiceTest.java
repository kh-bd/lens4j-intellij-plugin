package dev.khbd.lens4j.intellij.common.path;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.lens4j.common.Path;
import dev.khbd.lens4j.common.PathParser;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class PathServiceTest {

    private final PathParser pathParser = new PathParser();
    private final PathService service = new PathService();

    @DataProvider
    public static Object[][] validPathDataProvider() {
        return new Object[][]{
                {"pr1.pr2.pr3"},
                {"pr1.pr2.pr3."},
                {"pr1().pr2().pr3()"},
                {"pr1().pr2().pr3()."},
                {"pr1.pr2().pr3"},
                {"pr1.pr2.pr3()"},
                {"pr1().pr2.pr3"},
                {"pr1().pr2.pr3."}
        };
    }

    @Test(dataProvider = "validPathDataProvider")
    public void getCorrectPrefix_pathIsValid_returnTheSame(String str) {
        Path path = pathParser.parse(str);

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

    @Test(dataProvider = "validPathDataProvider")
    public void hasCorrectStructure_severalProperties_returnTrue(String str) {
        Path path = pathParser.parse(str);

        assertThat(service.hasCorrectStructure(path)).isTrue();
    }

}