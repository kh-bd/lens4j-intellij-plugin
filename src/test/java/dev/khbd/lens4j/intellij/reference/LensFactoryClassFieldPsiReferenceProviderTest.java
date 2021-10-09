package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import dev.khbd.lens4j.intellij.common.LensPsiUtil;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensFactoryClassFieldPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_factoryNotFound_notResolved() throws Exception {
        fixture.configureByFiles("reference/factory_field/field_found/Payment.java");

        PsiElement lensField = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(lensField).isNull();
    }

    @Test
    public void resolve_factoryFoundButStaticFieldNotFound_notResolved() throws Exception {
        fixture.configureByFiles("reference/factory_field/field_not_found/Payment.java",
                "reference/factory_field/field_not_found/PaymentLenses.java");

        PsiElement lensField = read(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();
            return reference.resolve();
        });

        assertThat(lensField).isNull();
    }

    @Test
    public void resolve_factoryFoundAndFieldFound_resolveToField() throws Exception {
        fixture.configureByFiles("reference/factory_field/field_found/Payment.java",
                "reference/factory_field/field_found/PaymentLenses.java");

        PsiElement lensField = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        assertThat(lensField).isNotNull();
        PsiField expectedField = read(() -> {
            PsiClass factoryClass = fixture.findClass("reference.factory_field.field_found.PaymentLenses");
            return LensPsiUtil.findField(factoryClass, "NAME_READ_LENS", true).get();
        });
        assertThat(lensField).isEqualTo(expectedField);
    }

    @Test
    public void resolve_factoryNameIsNotSuppliedButExistsFactoryAndField_deriveFactoryNameAndResolveField() throws Exception {
        fixture.configureByFiles("reference/factory_field/factory_name_empty/Payment.java",
                "reference/factory_field/factory_name_empty/PaymentLenses.java");

        PsiElement lensField = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        assertThat(lensField).isNotNull();
        PsiField expectedField = read(() -> {
            PsiClass factoryClass = fixture.findClass("reference.factory_field.factory_name_empty.PaymentLenses");
            return LensPsiUtil.findField(factoryClass, "NAME_READ_LENS", true).get();
        });
        assertThat(lensField).isEqualTo(expectedField);
    }
}