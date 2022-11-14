package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceFixture {

    public static void 노선의_이름이_조회된다(List<String> allLines, String expect) {
        assertThat(allLines).containsAnyOf(expect);
    }

    public static List<String> 모든_노선을_조회한다(String target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/")
                .then().log().all()
                .extract().jsonPath().getList(target, String.class);

    }

    public static ExtractableResponse<Response> 노선을_생성한다(String name, String color, long upStationId, long downStationId, int distance) {
        return RestAssured.given().log().all()
                .body(new LineRequest(name, color, upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

    }

    public static void 상태코드를_체크한다(int statusCode, int expect) {
        assertThat(statusCode).isEqualTo(expect);
    }
}
