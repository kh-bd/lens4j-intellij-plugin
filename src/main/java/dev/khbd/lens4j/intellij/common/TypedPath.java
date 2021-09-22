package dev.khbd.lens4j.intellij.common;

import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode(callSuper = true)
public class TypedPath extends Chain<TypedPathPart, TypedPath> {

    public Optional<TypedPathPart> findFirstPartWithUnResolvedField() {
        return stream()
                .filter(TypedPathPart::isFieldUnResolved)
                .findFirst();
    }
}
