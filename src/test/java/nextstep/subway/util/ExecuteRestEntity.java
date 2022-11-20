package nextstep.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
public class ExecuteRestEntity {

    public ValidatableResponse selectLine(String location) {
        return select(location);
    }

    public ValidatableResponse selectSection(String location) {
        return select(location);
    }

    public ValidatableResponse selectLines() {
        return select("/lines");
    }

    private ValidatableResponse select(String location) {
        return RestAssured.given().log().all()
                .when().get(location)
                .then().log().all();
    }

    public ExtractableResponse<Response> insertLineSuccess(LineRequest request) {
        ExtractableResponse<Response> response = insert(request, "/lines").extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response;
    }

    public ExtractableResponse<Response> insertSectionSuccess(String location, SectionRequest request) {
        ExtractableResponse<Response> response = insert(request, location).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response;
    }

    public ValidatableResponse insert(Object body, String path) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all();
    }

    public void updateLineSuccess(String location, LineRequest lineRequest) {
        ExtractableResponse<Response> response = updateLine(location, lineRequest).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public ValidatableResponse updateLine(String location, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(location)
                .then().log().all();
    }

    public void deleteLineSuccess(String location) {
        ExtractableResponse<Response> response = deleteLine(location).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public ValidatableResponse deleteLine(String location) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all();
    }

    public LineRequest generateLineRequest(String name, Long upStationId, Long downStationId) {
        return new LineRequest(name, "빨간색", upStationId, downStationId, 10);
    }

    public SectionRequest generateSectionRequest(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

}
