package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestUtil {
    private static final String LINE_BASE_PATH = "/lines";
    private static final String NAME = "name";
    private static final String ID = "id";

    public static ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        return given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LINE_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static List<String> 지하철노선_목록_조회() {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_BASE_PATH)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().jsonPath().getList(NAME, String.class);
    }

    public static ExtractableResponse<Response> 지하철노선을_조회(ExtractableResponse<Response> response) {
        return given().log().all()
            .pathParam(ID, response.jsonPath().getLong(ID))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_BASE_PATH + "/{id}")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static void 지하철노선을_수정한다(ExtractableResponse<Response> response, String updateName, String updateColor) {
        Map<String, String> params = new HashMap<>();
        params.put("name", updateName);
        params.put("color", updateColor);

        given().log().all()
            .pathParam(ID, response.jsonPath().getLong(ID))
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(LINE_BASE_PATH + "/{id}")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static void 지하철노선을_삭제한다(ExtractableResponse<Response> response) {
        given().log().all()
            .pathParam(ID, response.jsonPath().getLong(ID))
            .when().delete(LINE_BASE_PATH + "/{id}")
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .extract();
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재(List<String> actualNames, String expectNames) {
        assertThat(actualNames).contains(expectNames);
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재하지_않음(List<String> actualNames, String expectNames) {
        assertThat(actualNames).doesNotContain(expectNames);
    }

    public static void 지하철노선_검증_입력된_지하철노선이_존재(ExtractableResponse<Response> response, String lineName){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString(NAME)).isEqualTo(lineName);
    }
}
