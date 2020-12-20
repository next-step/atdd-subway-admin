package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.SafeStationInfo;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceStep {
    public static ExtractableResponse<Response> LINE_ALREADY_CREATED(final LineRequest lineRequest) {
        return REQUEST_CREATE_NEW_LINE(lineRequest);
    }

    public static ExtractableResponse<Response> REQUEST_CREATE_NEW_LINE(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> REQUEST_LINES() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> REQUEST_ONE_SPECIFIC_LINE(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static void LINE_CREATE_SUCCESS(
            ExtractableResponse<Response> response, String lineName, Long upStationId, Long downStationId
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        List<Long> responseStationIds = lineResponse.getStations().stream()
                .map(SafeStationInfo::getId)
                .collect(Collectors.toList());
        assertThat(responseStationIds).contains(upStationId, downStationId);
    }

    public static void LINES_INCLUDED_IN_LIST(
            ExtractableResponse<Response> line1CreatedResponse, ExtractableResponse<Response> line2CreatedResponse,
            ExtractableResponse<Response> listResponse
    ) {
        List<Long> expectedLineIds = Arrays.asList(line1CreatedResponse, line2CreatedResponse).stream()
                .map(LineAcceptanceStep::EXTRACT_ID_FROM_RESPONSE_LOCATION)
                .collect(Collectors.toList());
        List<Long> resultLineIds = listResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static ExtractableResponse<Response> REQUEST_LINE_UPDATE(Long lineId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> REQUEST_LINE_DELETE(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static Long EXTRACT_ID_FROM_RESPONSE_LOCATION(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("location").split("/")[2]);
    }

    public static void RESPONSE_INCLUDED_STATIONS(
            ExtractableResponse<Response> response, Long upStationId, Long downStationId
    ) {
        LineResponse lineResponse = response.as(LineResponse.class);

        List<Long> stationIds = lineResponse.getStations().stream()
                .map(SafeStationInfo::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).contains(upStationId, downStationId);
    }
}
