package dev.khbd.lens4j.intellij.inspection;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Sergei_Khadanovich
 */
public class DuplicateLensNamesInspectionTest extends BaseIntellijTest {


    @BeforeEach
    public void init() {
        fixture.enableInspections(DuplicateLensNamesInspection.class);
    }

    @Test
    public void inspect_explicitDuplicatedNamesExists_markThem() {
        fixture.configureByFiles("inspection/lens_name/explicit_duplicate/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_implicitDuplicatedName_markThem() {
        fixture.configureByFiles("inspection/lens_name/implicit_duplicate/name/Payment.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_implicitDuplicatedNameDerivedFromPath_markThem() {
        fixture.configureByFiles("inspection/lens_name/implicit_duplicate/path/Payment.java");

        fixture.testHighlighting(true, false, true);
    }
}
