package nextstep.subway.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamUtils {

    private StreamUtils() {}

    public static <T> Optional<T> filterAndFindFirst(Collection<T> collections,
        Predicate<T> predicate) {
        return collections.stream().filter(predicate).findFirst();
    }

    public static <T, R> List<R> mapToList(List<T> collections,  Function<T, R> function) {
        return collections.stream()
            .map(function)
            .collect(Collectors.toList());
    }
}
