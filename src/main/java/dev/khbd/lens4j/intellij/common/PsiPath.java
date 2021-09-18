package dev.khbd.lens4j.intellij.common;

import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode(callSuper = true)
public class PsiPath extends Chain<PsiPathElement, PsiPath> {

    public Optional<PsiPathElement> findFirstElementWithUnResolvedField() {
        return stream()
                .filter(PsiPathElement::isFieldUnResolved)
                .findFirst();
    }
}
