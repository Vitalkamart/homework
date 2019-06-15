package ru.sberbank.school.task11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StreamsTest {
    List<Integer> intList = new ArrayList<>();

    @BeforeEach
    void init() {
        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        intList.add(6);
        intList.add(7);
        intList.add(8);
        intList.add(9);
        intList.add(10);
    }

    @Test
    @DisplayName("filter method test")
    void filter() {
        Streams<Integer> integerStreams = Streams.of(intList);
        Predicate<Integer> predicate = i -> i > 9;
        Integer result = integerStreams.filter(predicate).toList().get(0);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("transform method test")
    void transform() {
        List<String> compareWith = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            compareWith.add("String" + i);
        }
        Streams<Integer> integerStreams = Streams.of(intList);
        Function<Integer, String> function1 = integer -> "String" + integer;
        assertEquals(integerStreams.transform(function1).toList(), compareWith);
    }

    @Test
    @DisplayName("sorting method test")
    void sorted() {
        Collections.reverse(intList);
        Streams<Integer> integerStreams = Streams.of(intList);
        Comparator<Integer> comparator = Comparator.reverseOrder();
        assertEquals(integerStreams.sorted(comparator).toList(), intList);
    }

    @Test
    @DisplayName("toMap method test")
    void toMap() {
        Streams<Integer> integerStreams = Streams.of(intList);
        Function<Integer, String> function1 = integer -> "String" + integer;
        Function<Integer, Integer> function2 = integer -> integer++;
        assertTrue(integerStreams.toMap(function1, function2) instanceof Map);
    }

    @Test
    @DisplayName("toSet method test")
    void toSet() {
        Streams<Integer> integerStreams = Streams.of(intList);
        assertTrue(integerStreams.toSet() instanceof Set);
    }

    @Test
    @DisplayName("toList method test")
    void toList() {
        Streams<Integer> integerStreams = Streams.of(intList);
        assertTrue(integerStreams.toList() instanceof List);
    }
}