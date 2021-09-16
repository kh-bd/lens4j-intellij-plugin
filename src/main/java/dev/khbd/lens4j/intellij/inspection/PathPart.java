package dev.khbd.lens4j.intellij.inspection;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
class PathPart {
    String property;
    int start;
    int end;
}
