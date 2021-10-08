package dev.khbd.lens4j.intellij.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.khbd.lens4j.intellij.BaseIntellijTest;
import dev.khbd.lens4j.intellij.reference.psi.LensPathPsiReference;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathPsiReferenceProviderTest extends BaseIntellijTest {

    @Test
    public void resolve_pathIsEmpty_noReference() {
        fixture.configureByFiles("reference/path/empty/Payment.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPosition();
            assertThat(reference).isNull();
        });
    }

    @Test
    public void resolve_pathHasWrongStructure_noReference() {
        fixture.configureByFiles("reference/path/wrong_structure/Payment.java");

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPosition();
            assertThat(reference).isNull();
        });
    }

    @Test
    public void resolve_pathIsCorrectAccountFieldRef_eachReferenceCanBeResolved() {
        fixture.configureByFiles("reference/path/correct/Account.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/AccountLensPayment.java"
        );

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();

            assertThat(reference).isInstanceOf(LensPathPsiReference.class);
            PsiElement field = reference.resolve();
            assertThat(field).isNotNull()
                    .isEqualTo(fixture.findClass("reference.path.correct.AccountLensPayment")
                            .findFieldByName("account", false));
        });
    }

    @Test
    public void resolve_pathIsCorrectCurrencyFieldRef_eachReferenceCanBeResolved() {
        fixture.configureByFiles("reference/path/correct/Account.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/CurrencyLensPayment.java"
        );

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();

            assertThat(reference).isInstanceOf(LensPathPsiReference.class);
            PsiElement field = reference.resolve();
            assertThat(field).isNotNull()
                    .isEqualTo(fixture.findClass("reference.path.correct.Account")
                            .findFieldByName("currency", false));
        });
    }

    @Test
    public void resolve_pathPrefixIsCorrectButLastPartIsWrong_eachPrefixReferenceCanBeResolved() {
        fixture.configureByFiles("reference/path/correct/Account.java",
                "reference/path/correct/Currency.java",
                "reference/path/correct/UnknownCurrencyFieldLensPayment.java"
        );

        ApplicationManager.getApplication().invokeLater(() -> {
            PsiReference reference = fixture.getReferenceAtCaretPositionWithAssertion();

            assertThat(reference).isInstanceOf(LensPathPsiReference.class);
            PsiElement field = reference.resolve();
            assertThat(field).isNull();
        });
    }
}
