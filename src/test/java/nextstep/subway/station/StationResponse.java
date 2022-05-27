package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationRequest.지하철역_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class StationResponse {
    public static void 지하철역_생성_성공(ExtractableResponse<Response> response) {
        String stationName = response.jsonPath().get("name");
        List<String> stationNames = 지하철역_조회_요청().jsonPath().getList("name", String.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationNames).contains(stationName);
    }

    public static void 지하철역_생성_요청_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_2개_조회_응답_성공(ExtractableResponse<Response> response) {
        List<String> stationNames = response.jsonPath().getList(".");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationNames).hasSize(2);
    }

    public static void 지하철역_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
