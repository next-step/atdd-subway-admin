package nextstep.subway.utils;

import io.restassured.response.Response;

public class ResponseBodyExtractUtils {

    public static final String ID = "id";

    public static String getString(Response response, String property) {
        return response.body().jsonPath().getString(property);
    }

    public static Long getIdAsLong(Response response) {
        return Long.parseLong(getString(response, ID));
    }
}
