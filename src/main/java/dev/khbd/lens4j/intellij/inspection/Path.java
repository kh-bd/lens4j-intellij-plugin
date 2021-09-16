package dev.khbd.lens4j.intellij.inspection;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode
class Path implements Iterable<PathPart> {

    final List<PathPart> parts = new ArrayList<>();

    Path addPart(PathPart part) {
        parts.add(part);
        return this;
    }

    @Override
    public Iterator<PathPart> iterator() {
        return parts.iterator();
    }
}
