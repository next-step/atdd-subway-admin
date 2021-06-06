package nextstep.subway.station;

import static nextstep.subway.utils.RestAssuredUtils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.domain.Station;

public class StationAcceptanceFixture {

    public static final String PATH = "/stations";

    public static Station 지하철역_생성_요청(final String name,
        final Function<ExtractableResponse<Response>, Station> function) {

        return function.apply(지하철역_생성_요청(name));
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return post(PATH, params);
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청(final String path) {
        return get(path);
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(final String path) {
        return delete(path);
    }

    public static Station toStation(final ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getObject(".", Station.class);
    }
}
