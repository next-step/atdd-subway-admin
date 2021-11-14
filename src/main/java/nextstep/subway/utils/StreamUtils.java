package nextstep.subway.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {
    }

    public static <T> Optional<T> filterAndFindFirst(Collection<T> collections, Predicate<T> predicate) {
        return collections.stream()
                          .filter(predicate)
                          .findFirst();
    }

    public static <T, R> List<R> mapToList(Collection<T> collections, Function<T, R> mapFunction) {
        return collections.stream()
                          .map(mapFunction)
                          .collect(Collectors.toList());
    }

    public static <T, R> List<R> flatMapToList(Collection<T> collections,
                                               Function<T, Collection<R>> mapFunction,
                                               Function<Collection<R>, Stream<R>> flatMapFunction) {
        return collections.stream()
                          .map(mapFunction)
                          .flatMap(flatMapFunction)
                          .distinct()
                          .collect(Collectors.toList());
    }
}
