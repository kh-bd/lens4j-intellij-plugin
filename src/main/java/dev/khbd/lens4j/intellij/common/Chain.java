package dev.khbd.lens4j.intellij.common;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @param <E> chain element type
 * @param <C> chain sub-type
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode
public class Chain<E, C extends Chain<E, C>> implements Iterable<E> {

    private final List<E> elements = new ArrayList<>();

    public C addElement(E element) {
        elements.add(element);
        return (C) this;
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public Stream<E> stream() {
        return elements.stream();
    }
}
