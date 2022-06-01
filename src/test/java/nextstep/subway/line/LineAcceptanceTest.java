package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("노선 관련 기능")
@Sql({"classpath:stations.sql"})
public class LineAcceptanceTest extends BaseAcceptanceTest {
    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final String lineName = "신분당선";

        // when
        final ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다(lineName);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<String> lineNames = 지하철_노선_목록을_조회한다();
        assertThat(lineNames).contains(lineName);
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
        final String lineName1 = "신분당선";
        final String lineName2 = "2호선";
        지하철_노선을_생성한다(lineName1);
        지하철_노선을_생성한다(lineName2);

        // when
        final List<String> lineNames = 지하철_노선_목록을_조회한다();

        // then
        assertThat(lineNames).containsExactly(lineName1, lineName2);
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(final String name) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
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

    private List<String> 지하철_노선_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }
}
