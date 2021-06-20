package nextstep.subway.line;

import static nextstep.subway.line.SectionAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceMethods {
    public static ExtractableResponse<Response> 지하철_구간_생성_요청(
        StationResponse upStation, StationResponse downStation, int distance) {
        SectionRequest request = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", 신분당선.getId())
            .then().log().all().extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제_요청(StationResponse station) {
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().delete("/lines/{lineId}/sections?stationId={stationId}", 신분당선.getId(), station.getId())
            .then().log().all().extract();
        return response;
    }

    public static void 지하철_구간_생성됨(ExtractableResponse<Response> response, StationResponse... stationsInOrder) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        List<StationResponse> actual = response.jsonPath().getList("stations", StationResponse.class);
        List<StationResponse> expected = new ArrayList<>(Arrays.asList(stationsInOrder));
        assertThat(actual).isEqualTo(expected);
    }

    public static void 지하철_구간_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_구간_삭제됨(ExtractableResponse<Response> response, StationResponse... stationsInOrder) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> lineResponse
            = LineAcceptanceMethods.지하철_노선_조회_요청(신분당선.getId());

        List<StationResponse> actual = lineResponse.jsonPath().getList("stations", StationResponse.class);
        List<StationResponse> expected = new ArrayList<>(Arrays.asList(stationsInOrder));
        assertThat(actual).isEqualTo(expected);
    }

    public static void 지하철_구간_삭제_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
