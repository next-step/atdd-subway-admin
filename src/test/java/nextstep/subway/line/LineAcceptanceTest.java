package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     **/
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 노선_생성결과 = 노선을_생성한다(노선_요청("1호선", "Blue", "서울역"));

        // then
        List<String> 노선_목록 = 노선_목록을_조회한다();
        String 노선명 = 노선_생성_결과에서_노선_이름을_조회한다(노선_생성결과);
        노선_목록_중_해당_노선을_찾을_수_있다(노선_목록, 노선명);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */

    @DisplayName("지하철노선 목록 조회")
    @Test
    void createLineAndFind() {
        // given
        ExtractableResponse<Response> 노선_생성_결과_2 = 노선을_생성한다(노선_요청("2호선", "Green", "까치산역"));
        ExtractableResponse<Response> 노선_생성_결과_3 = 노선을_생성한다(노선_요청("3호선", "Orange", "마두역"));

        // when
        List<String> 노선_목록 = 노선_목록을_조회한다();

        // then
        String 노선명2 = 노선_생성_결과에서_노선_이름을_조회한다(노선_생성_결과_2);
        String 노선명3 = 노선_생성_결과에서_노선_이름을_조회한다(노선_생성_결과_3);
        노선_목록_중_해당_노선을_찾을_수_있다(노선_목록, 노선명2);
        노선_목록_중_해당_노선을_찾을_수_있다(노선_목록, 노선명3);
    }

    private void 노선_목록_중_해당_노선을_찾을_수_있다(List<String> 노선_목록, String 노선명) {
        assertThat(노선_목록).contains(노선명);
    }


    private LineRequest 노선_요청(String 노선명, String 노션_색깔, String 지하철역) {
        ExtractableResponse<Response> 지하철역_생성_결과 = 지하철_역을_생성한다(지하철역);
        Long 지하철역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(지하철역_생성_결과);
        return new LineRequest(노선명, 노션_색깔, 지하철역_번호, 지하철역_번호, 30L);
    }

    public static String 노선_생성_결과에서_노선_이름을_조회한다(ExtractableResponse<Response> 노선_생성_결과) {
        return 노선_생성_결과.jsonPath()
                .getString("name");
    }

    private List<String> 노선_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

    }

    private ExtractableResponse<Response> 노선을_생성한다(LineRequest 노선_요청_정보) {
        return RestAssured.given().log().all()
                .body(노선_요청_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }
}
