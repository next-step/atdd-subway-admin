package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.utils.HttpUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineAcceptanceTestMethod {

    public static ExtractableResponse<Response> 신규_지하철_노선_생성_요청(String path, LineRequest lineRequest, int distance,
                                                                StationRequest upStations, StationRequest downStations) {

        Long upStationId = post("/stations", upStations).as(StationResponse.class).getId();
        Long downStationId = post("/stations", downStations).as(StationResponse.class).getId();
        lineRequest.setUpStationId(upStationId);
        lineRequest.setDownStationId(downStationId);
        lineRequest.setDistance(distance);

        return post(path, lineRequest);
    }

    public static void 지하철_노선_생성_확인(ExtractableResponse<Response> actualResponse, LineRequest excepted) {
        LineResponse actual = actualResponse.as(LineResponse.class);
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(excepted.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(excepted.getColor())
        );
    }

    public static void 지하철_노선_목록_확인(ExtractableResponse<Response> actual, List<Station> excepted) {
        List<LineResponse> responses = actual.jsonPath().getList(".", LineResponse.class);
        List<Station> actualStations = new ArrayList<>();
        responses.forEach(response -> {
            actualStations.addAll(response.getStations());
        });
        assertThat(actualStations).containsAll(excepted);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String path) {
        return get(path);
    }

    public static ExtractableResponse<Response> 지하철_노선_단건_조회(String path, Long request) {
        return get(path, request);
    }

    public static void 지하철_노선_단건_조회_확인(ExtractableResponse<Response> actual, Long excepted) {
        assertThat(actual.as(LineResponse.class).getId()).isEqualTo(excepted);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String path, LineRequest 이호선, Long id) {
        return put(path, 이호선, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(String path, Long id) {
        return delete(path, id);
    }

    public static void 응답_확인_OK(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 응답_확인_CREATED(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 응답_확인_BAD_REQUEST(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 응답_확인_NO_CONTENT(ExtractableResponse<Response> actual) {
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
