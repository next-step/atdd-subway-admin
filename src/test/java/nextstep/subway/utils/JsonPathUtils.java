package nextstep.subway.utils;

import java.util.List;

import com.jayway.jsonpath.JsonPath;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class JsonPathUtils {
    private JsonPathUtils() {
    }

    public static <T> List<T> extractList(ExtractableResponse<Response> response, String path) {
        return extract(response, path);
    }

    public static String extractString(ExtractableResponse<Response> response, String path) {
        return extract(response, path);
    }

    public static Integer extractInteger(ExtractableResponse<Response> response, String path) {
        return extract(response, path);
    }

    private static <T> T extract(ExtractableResponse<Response> response, String path) {
        return JsonPath.parse(response.body().asString()).read(path);
    }
}
