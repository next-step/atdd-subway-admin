package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LineSectionAcceptanceTestHelper {

    static ExtractableResponse<Response> createLineSection(Long lineId, Map<String, String> sectionRequestParams) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequestParams)
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();
    }

    static void assertOkStatus(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void assertInternalServerErrorStatus(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    static void assertInvalidDistanceMessage(ExtractableResponse<Response> response) {
        assertThat(getMessageFrom(response)).contains("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
    }

    static void assertConnectedStationNotPresentMessage(ExtractableResponse<Response> response) {
        assertThat(getMessageFrom(response)).contains("연결 할 수 없는 구간 입니다");
    }

    static void assertDuplicatedSectionMessage(ExtractableResponse<Response> response) {
        assertThat(getMessageFrom(response)).contains("이미 등록된 구간 입니다");
    }

    private static String getMessageFrom(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("message");
    }

    static void assertLineStationOrder(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);

        List<Long> stationIds = line.getStations().stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

}
