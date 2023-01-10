package dev.khbd.lens4j.intellij.inspection;

import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class DuplicateLensNamesInspectionTest extends BaseIntellijTest {


    @BeforeMethod
    @Override
    public void beforeMethod() throws Exception {
        super.beforeMethod();
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
