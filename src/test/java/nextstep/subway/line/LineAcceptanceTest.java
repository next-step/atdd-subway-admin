package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.common.BaseAcceptanceTest;
import nextstep.subway.common.ResponseAssertTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회시 생성한 노선을 찾을 수 있다.
     */
    @Test
    void 지하철노선_생성() {
        // When
        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성_요청("1호선", "bg-red-600","강남역", "역삼역", 10);

        // Then
        ResponseAssertTest.생성_확인(지하철노선_생성_응답);

        // Then
        ExtractableResponse<Response> linesResponse = 지하철노선_목록조회_요청();
        노선_포함_확인(linesResponse, new String[]{"1호선"});
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {
        // Given
        지하철노선_생성_요청("1호선", "bg-blue-600","강남역", "역삼역",  2);
        지하철노선_생성_요청("2호선", "bg-green-600","신촌역", "이대역",  2);

        // When
        ExtractableResponse<Response> 지하철노선_목록_응답 = 지하철노선_목록조회_요청();

        // When
        노선_포함_확인(지하철노선_목록_응답, new String[]{"1호선", "2호선"});
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_조회() {
        // Given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("1호선", "bg-blue-600", "강남역", "역삼역", 2);
        ResponseAssertTest.생성_확인(createResponse);

        // When
        ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회_요청(응답_ID(createResponse));

        // Then
        노선_정보_확인(지하철노선_조회_응답, 응답_ID(createResponse));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @Test
    void 지하철노선_수정() {
        // Given
        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성_요청("1호선", "bg-blue-600", "강남역", "역삼역", 2);
        ResponseAssertTest.생성_확인(지하철노선_생성_응답);

        // When
        Long id = 응답_ID(지하철노선_생성_응답);
        ExtractableResponse<Response> 지하철노선_수정_응답 = 지하철노선_수정_요청(id, "분당선", "bg-red-600");

        // Then
        지하철노선_수정_확인(id, 지하철노선_수정_응답, "분당선", "bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @Test
    void 지하철노선_삭제() {
        // Given
        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성_요청("1호선", "bg-blue-600", "강남역", "역삼역", 2);
        ResponseAssertTest.생성_확인(지하철노선_생성_응답);

        // When
        ExtractableResponse<Response> 지하철노선_삭제_응답 = 지하철노선_삭제_요청(응답_ID(지하철노선_생성_응답));

        // Then
        지하철노선_삭제_확인(응답_ID(지하철노선_생성_응답), 지하철노선_삭제_응답);
    }

    private void 지하철노선_삭제_확인(Long id, ExtractableResponse<Response> response) {
        ResponseAssertTest.응답_컨텐츠가_없는_성공_확인(response);
        ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();
        assertThat(getResponse.jsonPath().getList("id")).doesNotContain(id);
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String lineName, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = 응답_ID(지하철역_생성_요청(upStationName));
        Long downStationId = 응답_ID(지하철역_생성_요청(downStationName));

        HashMap<String, Object> lineMap = new HashMap<>();
        lineMap.put("name", lineName);
        lineMap.put("color", color);
        lineMap.put("upStationId", upStationId);
        lineMap.put("downStationId", downStationId);
        lineMap.put("distance", distance);

        return RestAssured.given().log().all()
                .body(lineMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_목록조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private void 노선_포함_확인(ExtractableResponse<Response> linesResponse, String[] lineNames) {
        assertAll(
                () -> {
                    List<String> responseLineNames = linesResponse.jsonPath().getList("name");
                    assertThat(responseLineNames).containsExactlyInAnyOrder(lineNames);
                }
        );
    }

    private void 노선_정보_확인(ExtractableResponse<Response> lineResponse, Long id) {
        assertAll(
                () -> ResponseAssertTest.성공_확인(lineResponse),
                () -> assertThat(lineResponse.jsonPath().getLong("id")).isEqualTo(id)
        );
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long lindId, String name, String color) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().patch("/lines/" + lindId)
                .then().log().all()
                .extract();
    }

    private void 지하철노선_수정_확인(Long id, ExtractableResponse<Response> updateResponse, String name, String color) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        ResponseAssertTest.성공_확인(updateResponse);
        ExtractableResponse<Response> response = 지하철노선_조회_요청(id);
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
