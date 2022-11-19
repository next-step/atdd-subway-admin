package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    LineRequest _1호선 = new LineRequest("1호선", "blue");
    LineRequest _2호선 = new LineRequest("2호선", "green");

    @BeforeEach
    public synchronized void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        long lineId = 지하철_노선을_생성한다(_1호선);

        // then
        지하철_노선_목록에서_조회된다(lineId);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void inquiryLines() {
        // given
        long lineId = 지하철_노선을_생성한다(_1호선);
        long lineId2 = 지하철_노선을_생성한다(_2호선);

        // when
        // then
        지하철_노선_목록에서_조회된다(lineId, lineId2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void inquiryLine() {
        // given
        long lineId = 지하철_노선을_생성한다(_1호선);

        // when
        // then
        지하철_노선_정보가_있다(lineId, _1호선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long lineId = 지하철_노선을_생성한다(_1호선);

        // when
        LineRequest modifiedLine = new LineRequest("1호선2", "black");
        지하철_노선을_수정한다(lineId, modifiedLine);

        // then
        지하철_노선_정보가_있다(lineId, modifiedLine);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        long lineId = 지하철_노선을_생성한다(_1호선);

        // when
        지하철_노선을_삭제한다(lineId);

        // then
        지하철_노선_정보가_없다(lineId);
    }

    private long 지하철_노선을_생성한다(LineRequest line) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(line)
            .when().post("/lines")
            .then().log().all()
            .extract()
            .jsonPath().getLong("id");
    }

    private void 지하철_노선_목록에서_조회된다(Long... lineId) {
        List<Long> lineIds = RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList("id", Long.class);
        assertThat(lineIds).containsAnyOf(lineId);
    }

    private void 지하철_노선_정보가_있다(Long lineId, LineRequest line) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().get("/lines/{id}", lineId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
        assertAll(
            () -> assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(lineId),
            () -> assertThat(response.jsonPath().getString("name"))
                .isEqualTo(line.getName()),
            () -> assertThat(response.jsonPath().getString("color"))
                .isEqualTo(line.getColor())
        );
    }

    private Map<String, Object> 지하철_노선을_수정한다(long lineId, LineRequest line) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(line)
            .when().put("/lines/{id}", lineId)
            .then().log().all()
            .extract()
            .jsonPath().getMap("");
    }

    private void 지하철_노선을_삭제한다(long lineId) {
        RestAssured.given().log().all()
            .when().delete("/lines/{id}", lineId)
            .then().log().all();
    }

    private void 지하철_노선_정보가_없다(long lineId) {
        Long id = RestAssured.given().log().all()
            .when().get("/lines/{id}", lineId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().get("id");
        assertThat(id).isNull();
    }
}
