package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_pathIsEmpty_noReference() throws Exception {
        fixture.configureByFiles("reference/path/empty/Payment.java");

        PsiReference ref = read(() -> fixture.getReferenceAtCaretPosition());

        assertThat(ref).isNull();
    }

    @Test
    public void resolve_pathHasWrongStructure_noReference() throws Exception {
        fixture.configureByFiles("reference/path/wrong_structure/Payment.java");

        PsiReference ref = read(() -> fixture.getReferenceAtCaretPosition());

        assertThat(ref).isNull();
    }

    @Test
    public void resolve_pathIsCorrectAccountFieldRef_eachReferenceCanBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/AccountLensPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement field = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        PsiField expectedField = read(() -> {
            PsiClass psiClass = fixture.findClass("reference.path.correct.AccountLensPayment");
            return psiClass.findFieldByName("account", false);
        });
        assertThat(field).isNotNull().isEqualTo(expectedField);
    }

    @Test
    public void resolve_pathIsCorrectCurrencyFieldRef_eachReferenceCanBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/CurrencyLensPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement field = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        PsiField expectedField = read(() -> {
            PsiClass psiClass = fixture.findClass("reference.path.correct.Account");
            return psiClass.findFieldByName("currency", false);
        });
        assertThat(field).isNotNull().isEqualTo(expectedField);
    }

    @Test
    public void resolve_pathPrefixIsCorrectButLastPartIsWrong_eachPrefixReferenceCanBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/UnknownCurrencyFieldLensPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement field = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(field).isNull();
    }

    @Test
    public void resolve_exactMethodExist_refToMethodCanBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/ExactMethodPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement method = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());
        PsiMethod expectedMethod = read(() -> {
            PsiClass psiClass = fixture.findClass("reference.path.correct.Account");
            return Arrays.stream(psiClass.findMethodsByName("getCurrency", true))
                    .findFirst()
                    .get();
        });
        assertThat(method).isNotNull().isEqualTo(expectedMethod);
    }

    @Test
    public void resolve_methodNotFound_refToMethodCanNotBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/MethodNotFoundPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement method = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(method).isNull();
    }

    @Test
    public void resolve_methodIsStatic_refToMethodCanNotBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/MethodIsStaticPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement method = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(method).isNull();
    }

    @Test
    public void resolve_methodIsPrivate_refToMethodCanNotBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/MethodIsPrivatePayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement method = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(method).isNull();
    }

    @Test
    public void resolve_methodHasArguments_refToMethodCanNotBeResolved() throws Exception {
        fixture.configureByFiles("reference/path/correct/MethodHasArgumentsPayment.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/Account.java"
        );

        PsiElement method = read(() -> fixture.getReferenceAtCaretPositionWithAssertion().resolve());

        assertThat(method).isNull();
    }
}
