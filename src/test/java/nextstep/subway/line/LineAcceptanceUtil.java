package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;

import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 Util")
public class LineAcceptanceUtil extends AcceptanceTest {

    private static LineRequest lineRequest;

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
//        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청(upStation);
//        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청(downStation);

        lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(long id, String name, String color) {
        lineRequest = new LineRequest(name, color);
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    public static List<LineResponse> 지하철_노선_전체_응답(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class);
    }

    public static Long 지하철_노선_생성_아이디_조회(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
