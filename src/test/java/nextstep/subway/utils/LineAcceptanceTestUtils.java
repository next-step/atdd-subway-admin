package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestUtils {
    public static final String 신분당선 = "신분당선";
    public static final String 경춘선 = "경춘선";
    public static final String 강남역 = "강남역";
    public static final String 신논현역 = "신논현역";
    public static final String 대성리역 = "대성리역";
    public static final String 가평역 = "가평역";
    private static final String BASE_PATH = "/lines";

    public static ExtractableResponse<Response> 지하철노선을_생성한다(LineRequest lineRequest) {
         return given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_PATH)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철노선_목록을_조회한다() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(BASE_PATH)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
    }

    public static void 지하철노선_목록_검증_입력된_지하철노선이_존재(List<String> lineNames, String... lineName) {
        assertThat(lineNames).contains(lineName);
    }
}
