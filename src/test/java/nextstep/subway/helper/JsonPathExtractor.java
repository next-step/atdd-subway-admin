package nextstep.subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

public class JsonPathExtractor {
    private JsonPathExtractor() { }

    public static int getTotalJsonArraySize(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getInt("size()");
    }

    public static Long getId(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }

    public static String getName(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getString("name");
    }

    public static List<String> getNames(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getList("name");
    }

    public static List<Object> getStations(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getList("stations");
    }

    public static List<Object> getStationNames(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getList("stations.name");
    }
}
