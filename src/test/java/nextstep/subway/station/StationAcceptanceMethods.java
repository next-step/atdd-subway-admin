package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

public class StationAcceptanceMethods extends AcceptanceTest {

    public static ExtractableResponse<Response> 지하철_역_생성_요청(String path, StationRequest stationRequest) {
        return post(path, stationRequest);
    }

    public static ExtractableResponse<Response> 지하철_역_등록되어_있음(String path, StationRequest stationRequest) {
        return post(path, stationRequest);
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청(String path) {
        return get(path);
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(String path) {
        return delete(path);
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedStationIds) {
        List<Long> resultStationIds = response.jsonPath()
                                              .getList(".", StationResponse.class)
                                              .stream()
                                              .map(StationResponse::getId)
                                              .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }
}
