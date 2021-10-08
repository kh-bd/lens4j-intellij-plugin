package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import dev.khbd.lens4j.intellij.reference.psi.FactoryClassPsiReference;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_factoryExists_referenceToFactory() {
        fixture.configureByFiles("reference/factory/Payment.java",
                "reference/factory/PaymentLenses.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();

            assertThat(reference).isInstanceOf(FactoryClassPsiReference.class);
            PsiElement factoryClassElement = reference.resolve();
            assertThat(factoryClassElement).isNotNull()
                    .isEqualTo(fixture.findClass("reference.factory.PaymentLenses"));
        });
    }

    @Test
    public void resolve_factoryNotFound_referenceCanNoBeResolved() {
        fixture.configureByFiles("reference/factory/Payment.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();

            assertThat(reference).isInstanceOf(FactoryClassPsiReference.class);
            PsiElement factoryClassElement = reference.resolve();
            assertThat(factoryClassElement).isNull();
        });
    }
}
