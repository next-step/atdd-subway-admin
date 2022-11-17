package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.UpdateLineRequest;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceFixture {

    private static final String DEFAULT_URL = "/lines";
    private static final String SLASH = "/";

    public static void 노선의_이름이_조회된다(List<String> allLines, String expect) {
        assertThat(allLines).containsAnyOf(expect);
    }

    public static List<String> 모든_노선을_조회한다(String target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getList(target, String.class);

    }

    public static ExtractableResponse<Response> 노선을_생성한다(String name, String color, long upStationId, long downStationId, int distance) {
        return RestAssured.given().log().all()
                .body(new LineRequest(name, color, upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();

    }

    public static long 노선을_생성후_ID_를_리턴한다(String name, String color, long upStationId, long downStationId, int distance) {
        ExtractableResponse<Response> response = 노선을_생성한다(name, color, upStationId, downStationId, distance);
        Object getId = response.jsonPath().get("id");

        return Long.parseLong(String.valueOf(getId));
    }

    public static ExtractableResponse<Response> 노선을_조회한다(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(DEFAULT_URL + SLASH + lineId)
                .then().log().all()
                .extract();
    }

    public static String 노선을_조회한후_이름을_리턴한다(Long lineId) {
        ExtractableResponse<Response> response = 노선을_조회한다(lineId);

        Object getId = response.jsonPath().get("name");

        return String.valueOf(getId);
    }

    public static ExtractableResponse<Response> 노선을_수정한다(Long lineId, String name, String color) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UpdateLineRequest(name, color))
                .when().put(DEFAULT_URL + SLASH + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_삭제한다(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(DEFAULT_URL + SLASH + lineId)
                .then().log().all()
                .extract();
    }

    public static void 노선의_이름이_일치하는지_확인한다(String name, String expect) {
        assertThat(name).isEqualTo(expect);
    }

    public static void 상태코드를_체크한다(int statusCode, int expect) {
        assertThat(statusCode).isEqualTo(expect);
    }

    public static void 노선이_올바르게_수정되었는지_체크한다(ExtractableResponse<Response> getLine, String expectLine, String expectColor) {
        assertThat(getLine.jsonPath().getString("name")).isEqualTo(expectLine);
        assertThat(getLine.jsonPath().getString("color")).isEqualTo(expectColor);
    }

    public static void 노선이_삭제되었는지_체크한다(List<String> name, String expect) {
        assertThat(name).doesNotContain(expect);
    }
}
