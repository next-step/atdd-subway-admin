package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철노선_생성후_조회() throws JsonProcessingException {
        // when
        ExtractableResponse<Response> postResponse = 지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() throws JsonProcessingException {
        // given
        ExtractableResponse<Response> postResponse1 = 지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        ExtractableResponse<Response> postResponse2 = 지하철노선_생성_요청("분당선", "bg-green-600", 3, 4, 10);

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("name")).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_조회() throws JsonProcessingException {
        // given
        ExtractableResponse<Response> postResponse = 지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(postResponse.jsonPath().getLong("id"));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() throws JsonProcessingException {
        // given
        ExtractableResponse<Response> postResponse = 지하철노선_생성_요청("5호선", "bg-purple-500", 7, 8, 10);
        long id = postResponse.body().jsonPath().getLong("id");
        // when
        String putRequestBody = objectMapper.writeValueAsString(new LineRequest("5호선", "bg-red-500", 1, 2, 10));
        ExtractableResponse<Response> putResponse = 지하철노선_수정_요청(id, putRequestBody);

        // then
        assertThat(putResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(putResponse.body().jsonPath().getString("color")).isEqualTo("bg-red-500");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() throws JsonProcessingException {
        // given
        ExtractableResponse<Response> postResponse = 지하철노선_생성_요청("3호선", "bg-orange-600", 5, 6, 10);
        // when
        ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(postResponse.jsonPath().getLong("id"));
        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, int upStationId, int downStationId,
                                                      int distance) throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(
                new LineRequest(name, color, upStationId, downStationId, distance));
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, String requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
