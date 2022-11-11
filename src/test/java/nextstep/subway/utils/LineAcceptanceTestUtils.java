package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.utils.CommonTestFixture.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestUtils {
    public static final String 신분당선 = "신분당선";
    public static final String 경춘선 = "경춘선";
    public static final String 수인분당선 = "수인분당선";
    public static final String 강남역 = "강남역";
    public static final String 신논현역 = "신논현역";
    public static final String 대성리역 = "대성리역";
    public static final String 가평역 = "가평역";

    public static ExtractableResponse<Response> 지하철노선을_생성한다(LineRequest lineRequest) {
         return given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_BASE_PATH)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철노선_목록을_조회한다() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(LINE_BASE_PATH)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(NAME, String.class);
    }

    public static ExtractableResponse<Response> 지하철노선을_조회한다(ExtractableResponse<Response> response) {
        return given().log().all()
                .pathParam(ID, response.jsonPath().getLong(ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(LINE_BASE_PATH + PATH_VARIABLE_ID)
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
                .params(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINE_BASE_PATH + PATH_VARIABLE_ID)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재(List<String> actualNames, String... lineNames) {
        assertThat(actualNames).contains(lineNames);
    }

    public static void 지하철노선_검증_입력된_지하철노선이_존재(ExtractableResponse<Response> response, String lineName){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString(NAME)).isEqualTo(lineName);
    }
}
