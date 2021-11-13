package nextstep.subway.common.utils;

public class ValidationUtils {

    public static boolean isEmpty(String str) {
        return isNull(str) || str.length() == 0;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

}
