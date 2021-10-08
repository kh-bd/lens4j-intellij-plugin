package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import dev.khbd.lens4j.intellij.reference.psi.FactoryClassFieldPsiReference;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassFieldPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_factoryNotFound_notResolved() {
        fixture.configureByFiles("reference/factory_field/field_found/Payment.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();
            assertThat(reference).isInstanceOf(FactoryClassFieldPsiReference.class);

            PsiElement lensField = reference.resolve();
            assertThat(lensField).isNull();
        });
    }

    @Test
    public void resolve_factoryFoundButStaticFieldNotFound_notResolved() {
        fixture.configureByFiles("reference/factory_field/field_not_found/Payment.java",
                "reference/factory_field/field_not_found/PaymentLenses.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();
            assertThat(reference).isInstanceOf(FactoryClassFieldPsiReference.class);

            PsiElement lensField = reference.resolve();
            assertThat(lensField).isNull();
        });
    }

    @Test
    public void resolve_factoryFoundAndFieldFound_resolveToField() {
        fixture.configureByFiles("reference/factory_field/field_found/Payment.java",
                "reference/factory_field/field_found/PaymentLenses.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();
            assertThat(reference).isInstanceOf(FactoryClassFieldPsiReference.class);

            PsiElement lensField = reference.resolve();
            PsiClass factoryClass = fixture.findClass("reference.factory_field.field_found.PaymentLenses");
            assertThat(lensField).isNotNull()
                    .isEqualTo(LensPsiUtil.findField(factoryClass, "NAME_READ_LENS", true));
        });
    }
}