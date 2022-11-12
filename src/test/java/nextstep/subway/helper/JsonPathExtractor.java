package nextstep.subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class JsonPathExtractor {
    public static int getTotalJsonArraySize(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getInt("size()");
    }
}
