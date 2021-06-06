package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;

public class LineAcceptanceFixture {

    enum LineFixture {
        FIRST(new Line(1L, "1호선", "bg-blue-600")),
        SECOND(new Line(2L, "2호선", "bg-green-600"));

        private final Line line;

        LineFixture(final Line line) {
            this.line = line;
        }

        Long getId() {
            return line.getId();
        }

        String getName() {
            return line.getName();
        }

        String getColor() {
            return line.getColor();
        }

        Line line() {
            return line;
        }
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(final String path, final LineFixture lineFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineFixture.getName());
        params.put("color", lineFixture.getColor());

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    static ExtractableResponse<Response> 지하철_노션_조회_요청(final String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정_요청(final String path, final LineFixture lineFixture) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", lineFixture.getName());
        params.put("color", lineFixture.getColor());

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_제거_요청(final String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    static Line toLine(final JsonPath jsonPath) {
        return new Line(jsonPath.getLong("id"), jsonPath.getString("name"), jsonPath.getString("color"));
    }
}
