package dev.khbd.lens4j.intellij.common;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode
public class Path implements Iterable<PathPart> {

    final List<PathPart> parts = new ArrayList<>();

    public Path addPart(PathPart part) {
        parts.add(part);
        return this;
    }

    @Override
    public Iterator<PathPart> iterator() {
        return parts.iterator();
    }
}
