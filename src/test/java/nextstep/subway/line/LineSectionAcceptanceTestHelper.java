package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LineSectionAcceptanceTestHelper {

    static ExtractableResponse<Response> createLineSection(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when().post("/lines/{lineId}/sections", line.getId())
            .then().log().all()
            .extract();
    }

    static ExtractableResponse<Response> removeLineStation(LineResponse line, StationResponse station) {
        return RestAssured
            .given().log().all()
            .when().delete("/lines/{lineId}/sections?stationId={stationId}", line.getId(), station.getId())
            .then().log().all()
            .extract();
    }

    static void assertOkStatus(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void assertInternalServerErrorStatus(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
