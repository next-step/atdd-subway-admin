package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.ResponseAssertTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineAcceptanceTest extends BaseAcceptanceTest {
    static final String rootPath = "/lines";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역");

        // then
        ResponseAssertTest.생성_확인(createResponse);

        // then
        ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();
        노선_포함_확인(getResponse, "7호선");
    }

    private void 노선_포함_확인(ExtractableResponse<Response> response, String lineName) {
        assertAll(
            () -> ResponseAssertTest.조회_확인(response),
            () -> {
                List<String> lineNames = response.jsonPath().getList("name");
                assertThat(lineNames).containsAnyOf(lineName);
            }
        );

    }

    private ExtractableResponse<Response> 지하철노선_목록조회_요청() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(rootPath)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, String upStationName, String downStationName) {
        Long upStationId = StationAcceptanceTest.지하철역_생성_요청(upStationName).jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철역_생성_요청(downStationName).jsonPath().getLong("id");

        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId);

        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(rootPath)
            .then().log().all()
            .extract();
    }
}
