package dev.khbd.lens4j.intellij.common;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class RawPathElement {
    String property;
    int start;
    int end;
}
