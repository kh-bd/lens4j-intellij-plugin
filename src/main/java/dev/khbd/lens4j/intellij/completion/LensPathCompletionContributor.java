package dev.khbd.lens4j.intellij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.lens4j.core.annotations.Lens;

/**
 * @author Sergei_Khadanovich
 */
public class LensPathCompletionContributor extends CompletionContributor {

    public LensPathCompletionContributor() {
        extend(CompletionType.BASIC,
                PsiJavaPatterns.psiElement(PsiJavaToken.class)
                        .withParent(PsiJavaPatterns.psiElement(PsiLiteralExpression.class))
                        .insideAnnotationParam(
                                StandardPatterns.string().equalTo(Lens.class.getCanonicalName()),
                                "path"
                        ), new LensPathCompletionProvider()
        );
    }
}
