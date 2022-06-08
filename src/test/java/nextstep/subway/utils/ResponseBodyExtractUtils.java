package nextstep.subway.utils;

import io.restassured.response.Response;
import java.util.List;

public class ResponseBodyExtractUtils {

    public static String getString(Response response, String property) {
        return response.body().jsonPath().getString(property);
    }

    public static List<String> getList(Response response, String property) {
        return response.body().jsonPath().getList(property, String.class);
    }
}
