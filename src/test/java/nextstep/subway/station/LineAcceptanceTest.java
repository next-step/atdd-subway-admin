package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 노선 관련기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private StationRepository stationRepository;

    private Station upStation;
    private Station downStation;
    private Long 분당선Id;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        upStation = stationRepository.save(new Station("왕십리역"));
        downStation = stationRepository.save(new Station("수원역"));

        final LineRequest 분당선_생성_Request = new LineRequest("분당선", upStation.getId(), downStation.getId(), "yellow", 1000L);
        ExtractableResponse<Response> 분당선_생성_응답 = 지하철노선_생성(분당선_생성_Request);
        분당선Id = 분당선_생성_응답.jsonPath().getLong("id");

        final LineRequest 이호선_생성_Request = new LineRequest("2호선", upStation.getId(), downStation.getId(), "green", 1000L);
        지하철노선_생성(이호선_생성_Request);
    }

    /**
     * when 지하철 노선을 생성하면
     * then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1호선__blue__1000"})
    @DisplayName("지하철 노선 생성 테스트")
    void 지하철노선_생성_테스트(String paramsText) {
        String[] params = paramsText.split("__");
        // given
        final LineRequest 노선생성_Request = new LineRequest(params[0], upStation.getId(), downStation.getId(), params[1], Long.valueOf(params[2]));
        final ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성(노선생성_Request);

        // when
        final ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회();

        // then
        assertThat(지하철노선_조회_응답.jsonPath().getList(".").contains(지하철노선_생성_응답.jsonPath().get())).isTrue();   // then
    }

    /**
     * given 지하철 노선을 생성하고
     * when 지하철 노선 목록을 조회하면
     * then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록 조회 테스트")
    void 지하철노선_목록_조회_테스트() {
        // when
        final ExtractableResponse<Response> 지하철목록_응답 = 지하철노선_조회();
        // then
        assertThat(지하철목록_응답.jsonPath().getList(".").size()).isEqualTo(2);
    }

    /**
     * given 지하철 노선을 생성하고
     * when 지하철 노선 목록을 수정하면
     * then 해당 지하철 노선 정보는 수정된다.
     */
    @Test
    @DisplayName("지하철노선 수정 테스트")
    void 지하철노선_수정_테스트() {
        // when
        ExtractableResponse<Response> 분당선_수정_응답 = 지하철노선_수정(분당선Id, new LineRequest("3호선", "purple"));
        // given
        ExtractableResponse<Response> 분당선_상세_응답 = 지하철노선_상세_조회(분당선Id);

        assertThat(분당선_수정_응답.jsonPath().getString("color")).isEqualTo(분당선_상세_응답.jsonPath().getString("color"));
    }

    private ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        final ExtractableResponse<Response> 지하철노선_생성_응답 =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/line")
                        .then().log().all()
                        .extract();

        지하철노선_생성_검증(지하철노선_생성_응답);

        return 지하철노선_생성_응답;
    }

    private void 지하철노선_생성_검증(ExtractableResponse<Response> 지하철노선_생성_응답) {
        assertThat(지하철노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> 지하철노선_조회() {
        final ExtractableResponse<Response> 지하철노선_조회_응답 =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/line")
                        .then().log().all()
                        .extract();

        지하철노선_조회_검증(지하철노선_조회_응답);

        return 지하철노선_조회_응답;
    }

    private void 지하철노선_조회_검증(ExtractableResponse<Response> 지하철노선_조회_응답) {
        assertThat(지하철노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철노선_수정(Long lineId, LineRequest lineRequest) {
        final ExtractableResponse<Response> 지하철노선_수정_응답 =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put("/line")
                        .then().log().all()
                        .extract();

        지하철노선_수정_검증(지하철노선_수정_응답);

        return 지하철노선_수정_응답;
    }

    private void 지하철노선_수정_검증(ExtractableResponse<Response> 지하철노선_수정_응답) {
        assertThat(지하철노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철노선_상세_조회(Long lineId) {
        final ExtractableResponse<Response> 지하철노선_상세_응답 =
                RestAssured.given().log().all()
                        .when().get("/line/" + lineId)
                        .then().log().all()
                        .extract();

        지하철노선_상세_검증(지하철노선_상세_응답);
        return 지하철노선_상세_응답;
    }

    private void 지하철노선_상세_검증(ExtractableResponse<Response> 지하철노선_상세_응답) {
        assertThat(지하철노선_상세_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
