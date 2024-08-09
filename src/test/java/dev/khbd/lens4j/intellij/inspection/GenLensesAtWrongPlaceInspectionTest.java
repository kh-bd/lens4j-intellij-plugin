package dev.khbd.lens4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class GenLensesAtWrongPlaceInspectionTest extends BaseIntellijTest {

    @BeforeEach
    public void init() {
        fixture.enableInspections(GenLensesAtWrongPlaceInspection.class);
    }

    @Test
    public void inspect_genLensOnInterface_inspectError() {
        fixture.configureByFiles("inspection/wrong_place/on_interface/Fly.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(1);
        IntentionAction removeAnnotationFix = intentions.get(0);
        fixture.launchAction(removeAnnotationFix);
        fixture.checkResultByFile("inspection/wrong_place/on_interface/Fly_after.java");
    }

    @Test
    public void inspect_genLensOnGenericClass_inspectError() {
        fixture.configureByFiles("inspection/wrong_place/on_gen_class/AbstractPayment.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(1);
        IntentionAction removeAnnotationFix = intentions.get(0);
        fixture.launchAction(removeAnnotationFix);
        fixture.checkResultByFile("inspection/wrong_place/on_gen_class/AbstractPayment_after.java");
    }
}