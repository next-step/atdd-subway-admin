package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.common.AcceptanceTest;
import nextstep.subway.common.RestAssuredTemplate;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();

        StationAcceptanceTest.지하철역을_생성한다("강남역");
        StationAcceptanceTest.지하철역을_생성한다("잠실역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철_노선을_생성한다("2호선", "green lighten-3");

        // then
        List<String> lineNames = 모든_지하철_노선을_조회한다();
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선의 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선을_생성한다("2호선", "green lighten-3");
        지하철_노선을_생성한다("8호선", "pink lighten-3");

        // when
        List<String> lineNames = 모든_지하철_노선을_조회한다();

        // then
        assertThat(lineNames).hasSize(2);
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
        ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다("2호선", "green lighten-3");
        long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        String lineName = 지하철_노선을_조회한다(lineId);

        // then
        assertThat(lineName).isEqualTo("2호선");
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
        ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다("2호선", "green lighten-3");
        long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선을_수정한다(lineId, "8호선", "pink lighten-3");

        // then
        지하철_노선_정보가_수정된다(response);
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
        ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다("2호선", "green lighten-3");
        long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선을_삭제한다(lineId);

        // then
        지하철_노선이_삭제된다(response);
    }

    public static ExtractableResponse<Response> 지하철_노선을_생성한다(String name, String color) {
        return RestAssuredTemplate.post("/lines", new LineRequest(name, color, 1, 2, 10));
    }

    public static List<String> 모든_지하철_노선을_조회한다() {
        return RestAssuredTemplate.get("/lines").jsonPath().getList("name", String.class);
    }

    public static String 지하철_노선을_조회한다(long lineId) {
        return RestAssuredTemplate.get("/lines/" + lineId).jsonPath().getString("name");
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(long lineId, String name, String color) {
        return RestAssuredTemplate.put("/lines/" + lineId, new LineRequest(name, color, 1, 2, 10));
    }

    public static ExtractableResponse<Response> 지하철_노선을_삭제한다(long lineId) {
        return RestAssuredTemplate.delete("/lines/{id}", lineId);
    }

    private void 지하철_노선_정보가_수정된다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선이_삭제된다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
