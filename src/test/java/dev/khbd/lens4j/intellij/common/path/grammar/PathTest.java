package dev.khbd.lens4j.intellij.common.path.grammar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class PathTest {

    private final PathParser pathParser = PathParser.getInstance();

    @Test
    public void isStructureCorrect_pathIsEmpty_returnTrue() {
        Path path = Path.empty();

        assertThat(path.isStructureCorrect()).isTrue();
    }

    @Test
    public void isStructureCorrect_onlyPoint_returnFalse() {
        Path path = pathParser.parse(".");

        assertThat(path.isStructureCorrect()).isFalse();
    }

    @Test
    public void isStructureCorrect_startsFromPoint_returnFalse() {
        Path path = pathParser.parse(".pr");

        assertThat(path.isStructureCorrect()).isFalse();
    }

    @Test
    public void isStructureCorrect_startsFromPointButWithMethod_returnFalse() {
        Path path = pathParser.parse(".pr()");

        assertThat(path.isStructureCorrect()).isFalse();
    }

    @Test
    public void isStructureCorrect_severalPointsInTheMiddle_returnFalse() {
        Path path = pathParser.parse("pr1...pr2");

        assertThat(path.isStructureCorrect()).isFalse();
    }

    @Test
    public void isStructureCorrect_severalPointsInTheMiddleButWithMethod_returnFalse() {
        Path path = pathParser.parse("pr1()...pr2()");

        assertThat(path.isStructureCorrect()).isFalse();
    }

    @Test
    public void isStructureCorrect_endsWithPoint_returnTrue() {
        Path path = pathParser.parse("pr1.pr2.");

        assertThat(path.isStructureCorrect()).isTrue();
    }

    @Test
    public void isStructureCorrect_endsWithPointButWithMethods_returnTrue() {
        Path path = pathParser.parse("pr1().pr2().");

        assertThat(path.isStructureCorrect()).isTrue();
    }

    @Test
    public void isStructureCorrect_severalProperties_returnTrue() {
        Path path = pathParser.parse("pr1.pr2.pr3.pr4");

        assertThat(path.isStructureCorrect()).isTrue();
    }

    @Test
    public void isStructureCorrect_severalMethods_returnTrue() {
        Path path = pathParser.parse("pr1().pr2().pr3().pr4()");

        assertThat(path.isStructureCorrect()).isTrue();
    }

    public static List<String> validPathDataProvider() {
        return List.of(
                "pr1.pr2.pr3",
                "pr1.pr2.pr3.",
                "pr1().pr2().pr3()",
                "pr1().pr2().pr3().",
                "pr1.pr2().pr3",
                "pr1.pr2.pr3()",
                "pr1().pr2.pr3",
                "pr1().pr2.pr3."
        );
    }

    @ParameterizedTest
    @MethodSource("validPathDataProvider")
    public void correctPrefix_pathIsValid_returnTheSame(String str) {
        Path path = pathParser.parse(str);

        Path result = path.correctPrefix();

        assertThat(result).isEqualTo(path);
    }

    @Test
    public void correctPrefix_pathContainsSeveralPoints_returnPrefix() {
        Path path = pathParser.parse("pr1.pr2...pr3");

        Path result = path.correctPrefix();

        assertThat(result).isEqualTo(pathParser.parse("pr1.pr2."));
    }

    @Test
    public void correctPrefix_pathStartsFromPoint_returnEmpty() {
        Path path = pathParser.parse(".pr1.pr2");

        Path result = path.correctPrefix();

        assertThat(result).isEqualTo(Path.empty());
    }
}
