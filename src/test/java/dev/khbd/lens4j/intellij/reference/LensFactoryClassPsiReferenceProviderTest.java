package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_factoryExists_referenceToFactory() throws Exception {
        fixture.configureByFiles("reference/factory/Payment.java",
                "reference/factory/PaymentLenses.java");

        PsiElement factoryClassElement =
                read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        PsiClass expectedClass = read(() -> fixture.findClass("reference.factory.PaymentLenses"));

        assertThat(factoryClassElement).isNotNull()
                .isEqualTo(expectedClass);
    }

    @Test
    public void resolve_factoryNotFound_referenceCanNoBeResolved() throws Exception {
        fixture.configureByFiles("reference/factory/Payment.java");

        PsiElement factoryClassElement =
                read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(factoryClassElement).isNull();
    }
}
