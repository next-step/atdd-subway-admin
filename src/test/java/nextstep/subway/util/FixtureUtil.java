package nextstep.subway.util;

public class FixtureUtil {

    private FixtureUtil() {
    }

    public static Long getIdFromLocation(final String location) {
        String id = location.substring(location.lastIndexOf("/") + 1);
        return Long.parseLong(id);
    }
}
