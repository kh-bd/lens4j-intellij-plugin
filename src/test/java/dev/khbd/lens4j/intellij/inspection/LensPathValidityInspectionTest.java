package dev.khbd.lens4j.intellij.inspection;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathValidityInspectionTest extends BaseIntellijTest {

    @BeforeEach
    public void init() {
        fixture.enableInspections(LensPathValidityInspection.class);
    }

    @Test
    public void inspect_pathIsEmpty_verifyError() {
        fixture.configureByFiles("inspection/path/path_is_empty/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_fullPathIsResolved_verifyInfo() {
        fixture.configureByFiles("inspection/path/valid/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_prefixIsResolved_verifyError() {
        fixture.configureByFiles("inspection/path/prefix_valid/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_lestIsWriteAndMethodsUsedAtLastPosition_verifyError() {
        fixture.configureByFiles("inspection/path/method_at_last_position/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_lestIsWriteAndFinalFieldsUsedAtLastPosition_verifyError() {
        fixture.configureByFiles("inspection/path/final_field_at_last_position/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_arrayLength_verify() {
        fixture.configureByFiles("inspection/path/array_length/Payment.java");

        fixture.testHighlighting(true, false, true);
    }
}