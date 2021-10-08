package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import org.testng.annotations.Test;

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
}
