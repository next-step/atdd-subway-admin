package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.ResponseAssertTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
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
        노선_포함_확인(getResponse, new String[]{"7호선"});
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선_생성_요청("7호선", "green", "수락산역", "마들역");
        지하철노선_생성_요청("신분당선", "red", "강남역", "논현역");

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();

        // then
        노선_포함_확인(getResponse, new String[]{"7호선", "신분당선"});
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역");

        // when
        Long id = createResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> getResponse = 지하철노선_단건조회_요청(id);

        // then
        노선_조회_확인(getResponse, id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역");

        // when
        Long id = createResponse.jsonPath().getLong("id");
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("1호선", "blue");
        ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(id, lineUpdateRequest);

        // then
        노선_수정_확인(id, updateResponse, lineUpdateRequest);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역");

        // when
        Long id = createResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(id);

        // then
        노선_삭제_확인(id, deleteResponse);
    }

    private void 노선_삭제_확인(Long id, ExtractableResponse<Response> response) {
        assertAll(
            () -> ResponseAssertTest.빈응답_확인(response),
            () -> {
                ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();

                assertThat(getResponse.jsonPath().getList("id")).doesNotContain(id);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_수정_확인(Long id, ExtractableResponse<Response> response, LineUpdateRequest lineUpdateRequest) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                ExtractableResponse<Response> getResponse = 지하철노선_단건조회_요청(id);

                assertThat(getResponse.jsonPath().getString("name")).isEqualTo(lineUpdateRequest.getName());
                assertThat(getResponse.jsonPath().getString("color")).isEqualTo(lineUpdateRequest.getColor());
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
            .body(lineUpdateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().patch(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_조회_확인(ExtractableResponse<Response> response, Long id) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                assertThat(response.jsonPath().getLong("id")).isEqualTo(id);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_단건조회_요청(long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_포함_확인(ExtractableResponse<Response> response, String[] lineName) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                List<String> lineNamesAll = response.jsonPath().getList("name");
                assertThat(lineNamesAll).containsExactlyInAnyOrder(lineName);
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
