package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestResponse {

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성됨(Long firstStationId, Long secondStationId, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(LineResponse.class).getStations()).hasSize(2);
        assertThat(response.as(LineResponse.class).getStations().get(0).getId()).isSameAs(firstStationId);
        assertThat(response.as(LineResponse.class).getStations().get(1).getId()).isSameAs(secondStationId);
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선_조회됨(ExtractableResponse<Response> extractableResponse, LineRequest request) {
        LineResponse linesResponse = extractableResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(linesResponse.getName()).isEqualTo(request.getName());
        assertThat(linesResponse.getColor()).isEqualTo(request.getColor());
        assertThat(linesResponse.getStations().size()).isEqualTo(2);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

}
