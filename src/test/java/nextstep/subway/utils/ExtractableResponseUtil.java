package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractableResponseUtil {

    public static Long extractIdInResponse(ExtractableResponse response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static List<Long> extractIdInResponses(ExtractableResponse ... responses) {
        return Arrays.asList(responses).stream()
                .map(ExtractableResponseUtil::extractIdInResponse)
                .collect(Collectors.toList());
    }

    public static <T> T extractObjectInResponse(ExtractableResponse response) {
        return null;
    }

    public static <T> List<T> extractObjectsInResponse(ExtractableResponse response) {
        return null;
    }
}
