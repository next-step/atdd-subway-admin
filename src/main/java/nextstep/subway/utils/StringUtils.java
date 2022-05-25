package nextstep.subway.utils;

public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
