package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("노선 관련 기능")
@Sql({"classpath:line_acceptance_test.sql"})
public class LineAcceptanceTest extends BaseAcceptanceTest {
    private final long GANGNAM = 1L;
    private final long YANGJAE = 2L;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다("신분당선");

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<LineResponse> lines = 지하철_노선_목록을_조회한다().jsonPath().getList(".", LineResponse.class);
        assertThat(lines).contains(createResponse.body().as(LineResponse.class));
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final LineResponse createdLine1 = 지하철_노선을_생성한다("신분당선").body().as(LineResponse.class);
        final LineResponse createdLine2 = 지하철_노선을_생성한다("2호선").body().as(LineResponse.class);

        // when
        final ExtractableResponse<Response> getListResponse = 지하철_노선_목록을_조회한다();

        // then
        assertThat(getListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getListResponse.jsonPath().getList(".", LineResponse.class))
                .containsExactly(createdLine1, createdLine2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final LineResponse createdLine = 지하철_노선을_생성한다("신분당선").body().as(LineResponse.class);

        // when
        final ExtractableResponse<Response> getResponse = 지하철_노선을_조회한다(createdLine.getId());

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.body().as(LineResponse.class)).isEqualTo(createdLine);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        final LineResponse createdLine = 지하철_노선을_생성한다("신분당선").body().as(LineResponse.class);
        final String newName = "수정된이름";
        final String newColor = "bg-modified-600";

        // when
        final ExtractableResponse<Response> putResponse = 지하철_노선을_수정한다(createdLine.getId(), newName, newColor);

        // then
        assertThat(putResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final LineResponse modifiedLine = 지하철_노선을_조회한다(createdLine.getId()).body().as(LineResponse.class);
        assertThat(modifiedLine.getName()).isEqualTo(newName);
        assertThat(modifiedLine.getColor()).isEqualTo(newColor);
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
        final LineResponse createdLine = 지하철_노선을_생성한다("신분당선").body().as(LineResponse.class);

        // when
        final ExtractableResponse<Response> deleteResponse = 지하철_노선을_삭제한다(createdLine.getId());

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final ExtractableResponse<Response> getResponse = 지하철_노선을_조회한다(createdLine.getId());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(final String name) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", GANGNAM);
        params.put("downStationId", YANGJAE);
        params.put("distance", 10);

        // when
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선을_조회한다(final Long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선을_수정한다(final Long id, final String newName, final String newColor) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", newName);
        params.put("color", newColor);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선을_삭제한다(final Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
