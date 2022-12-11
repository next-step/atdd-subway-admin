package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AbstractAcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관리 기능")
public class LineAcceptanceTest extends AbstractAcceptanceTest{
    private Long upStationId;
    private Long downStationId;

    LineRequest lineRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        upStationId = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId();
        downStationId = StationAcceptanceTest.지하철역_생성("공릉역").as(StationResponse.class).getId();
        lineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
    }

    /*
     *   When 지하철 노선을 생성하면
     *   Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    public void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
     *   Given 2개의 지하철 노선을 생성하고
     *   When 지하철 노선 목록을 조회하면
     *   Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    public void getLines() {
        // given
        LineRequest firstLineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        LineRequest secondLineRequest = new LineRequest("분당선", "green", upStationId, downStationId, 10);

        지하철_노선_생성(firstLineRequest);
        지하철_노선_생성(secondLineRequest);
        // when
        List<String> lines = 지하철_노선_목록_조회();
        // then
        assertThat(lines)
                .hasSize(2)
                .containsAnyOf("신분당선", "분당선");
    }

    /*
     *   Given 지하철 노선을 생성하고
     *   When 생성한 지하철 노선을 조회하면
     *   Then 생성한 지하철 노선의 정보를 응받받을 수 있다.
     */
    @DisplayName("아이디로 지하철 노선을 조회한다.")
    @Test
    public void getLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);
        // when
        String lineName = 지하철_노선_조회(response.jsonPath().getString("id"));

        // then
        assertThat(lineName).isEqualTo(lineRequest.getName());
    }

    /*
     *   Given 지하철 노선을 생성하고
     *   When 생성한 지하철 노선을 수정하면
     *   Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    public void updateLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);
        LineRequest updateLineRequest = new LineRequest("7호선", "green", upStationId, downStationId, 10);
        // when
        JsonPath line = 지하철_노선_수정(updateLineRequest, response.jsonPath().getString("id"));
        // then
        assertAll(
                () -> assertThat(line.getString("name")).isEqualTo(updateLineRequest.getName()),
                () -> assertThat(line.getString("color")).isEqualTo(updateLineRequest.getColor())
        );
    }

    /*
     *   Given 지하철 노선을 생성하고
     *   When 생성한 지하철 노선을 삭제하면
     *   Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제 한다")
    @Test
    public void deleteLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선_생성(lineRequest);
        // when
        지하철_노선_삭제(response.body().jsonPath().getInt("id"));
        // then
        List<String> lines = 지하철_노선_목록_조회();

        assertThat(lines).isEmpty();
    }

    public static String 지하철_노선_조회(String id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract().path("name");
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/liens")
                        .then().log().all()
                        .extract();

        return response;
    }

    public List<String> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private JsonPath 지하철_노선_수정(LineRequest updateLineRequest, String id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(updateLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract().jsonPath();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(int id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

}
