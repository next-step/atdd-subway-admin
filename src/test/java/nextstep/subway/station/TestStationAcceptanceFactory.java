package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestStationAcceptanceFactory {

    public static StationResponse 지하철_역_생성(ExtractableResponse<Response> response) {
        return response.as(StationResponse.class);
    }

    public static List<Long> 지하철_역_목록_IDs_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @SafeVarargs
    public static List<Long> 지하철_역_IDs_추출(ExtractableResponse<Response>... createResponse) {
        return Arrays.stream(createResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    public static long 지하철_역_ID_추출(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    public static void 지하철_역_목록_포함됨(List<Long> expectedLineIds, List<Long> resultLineIds) {
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static StationRequest 지하철_역_파라미터_생성(String name) {
        return StationRequest.from(name);
    }

    public static void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
