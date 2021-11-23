package nextstep.subway.station;

import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredBuilder;
import org.springframework.http.MediaType;

public class StationTestFixture {
    public static final String BASE_STATION_URL = "/stations";

    private StationTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static StationRequest 강남역_요청_데이터() {
        return new StationRequest("강남역");
    }

    public static StationRequest 역삼역_요청_데이터() {
        return new StationRequest("역삼역");
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return new RestAssuredBuilder(Method.POST, BASE_STATION_URL)
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBody(stationRequest)
                .build();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return new RestAssuredBuilder(Method.GET, BASE_STATION_URL)
                .build();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(StationResponse stationResponse) {
        return new RestAssuredBuilder(Method.DELETE, BASE_STATION_URL + "/" + stationResponse.getId())
                .build();
    }

    public static StationResponse 지하철역_등록되어_있음(StationRequest stationRequest) {
        return 지하철역_생성_요청(stationRequest)
                .as(StationResponse.class);
    }
}
