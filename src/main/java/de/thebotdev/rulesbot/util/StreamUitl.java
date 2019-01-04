package de.thebotdev.rulesbot.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByKey;

public class StreamUitl {

    public static <T> Stream<Stream<T>> chunkedBySize(Stream<T> stream, int chunkSize) {
        AtomicInteger integer = new AtomicInteger();
        return stream.collect(Collectors.groupingBy(ignored -> integer.getAndIncrement() / chunkSize))
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .map(Map.Entry::getValue)
                .map(List::stream);
    }

    public static <T> Stream<Stream<T>> chunkedByCount(Stream<T> stream, int chunkCount) {
        AtomicInteger integer = new AtomicInteger();
        return stream.collect(Collectors.groupingBy(ignored -> integer.getAndIncrement() % chunkCount))
                .values()
                .stream()
                .map(List::stream);
    }

}
