package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.getResponseId;
import static nextstep.subway.station.StationAcceptanceTest.givenCreateStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.ResponseAssertTest;
import nextstep.subway.domain.PathConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        // When
        ExtractableResponse<Response> lineResponse = 지하철노선_생성_요청("1호선", "bg-red-600","강남역", "역삼역", 10);

        // Then
        ResponseAssertTest.생성_확인(lineResponse);

        // Then
        ExtractableResponse<Response> linesResponse = 지하철노선_목록조회_요청();
        노선_포함_확인(linesResponse, new String[]{"1호선"});
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getLines() {
        // Given
        지하철노선_생성_요청("1호선", "bg-red-600","강남역", "역삼역",  2);
        지하철노선_생성_요청("2호선", "bg-red-600","신촌역", "이대역",  2);

        // When
        ExtractableResponse<Response> linesResponse = 지하철노선_목록조회_요청();

        // When
        노선_포함_확인(linesResponse, new String[]{"1호선", "2호선"});
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getLine() {
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String lineName, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = getResponseId(givenCreateStation(upStationName));
        Long downStationId = getResponseId(givenCreateStation(downStationName));

        HashMap<String, Object> lineMap = new HashMap<>();
        lineMap.put("name", lineName);
        lineMap.put("color", color);
        lineMap.put("upStationId", upStationId);
        lineMap.put("downStationId", downStationId);
        lineMap.put("distance", distance);

        return RestAssured.given().log().all()
                .body(lineMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PathConstant.LINE_ROOT_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_목록조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(PathConstant.LINE_ROOT_PATH)
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
}
