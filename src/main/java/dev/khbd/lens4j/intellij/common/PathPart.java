package dev.khbd.lens4j.intellij.common;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class PathPart {
    String property;
    int start;
    int end;
}