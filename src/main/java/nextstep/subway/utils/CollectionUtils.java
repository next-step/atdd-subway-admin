package nextstep.subway.utils;

import java.util.List;

import static nextstep.subway.utils.NumberUtils.ONE;
import static nextstep.subway.utils.NumberUtils.ZERO;

public final class CollectionUtils {

    public static <T> T getFirst(List<T> list) {
        return list.get(ZERO);
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - ONE);
    }

    public static <T> boolean hasOneSize(List<T> list)  {
        return list.size() == ONE;
    }
}
