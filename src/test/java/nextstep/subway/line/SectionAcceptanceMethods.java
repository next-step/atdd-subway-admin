package nextstep.subway.line;

import static nextstep.subway.line.SectionAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceMethods {

    public static void 지하철_구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // TODO 응답값이 존재할 경우 추가 검증
    }

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
}
