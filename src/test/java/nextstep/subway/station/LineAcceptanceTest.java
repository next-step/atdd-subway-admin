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

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        upStation = stationRepository.save(new Station("왕십리역"));
        downStation = stationRepository.save(new Station("수원역"));

    }

    /**
     * - when 지하철 노선을 생성하면
     * - then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 생성 테스트")
    void 지하철노선_생성_테스트() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", upStation.getId(), downStation.getId(), "yellow", 1000L);

        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성(lineRequest);

        // when
        ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회();

        // then
        assertThat(지하철노선_조회_응답.jsonPath().getList(".").contains(지하철노선_생성_응답.jsonPath().get())).isTrue();   // then
    }

    private ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        ExtractableResponse<Response> 지하철노선_생성_응답 =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/line")
                        .then().log().all()
                        .extract();

        지하철역_생성_검증(지하철노선_생성_응답);

        return 지하철노선_생성_응답;
    }

    private void 지하철역_생성_검증(ExtractableResponse<Response> 지하철노선_생성_응답) {
        assertThat(지하철노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> 지하철노선_조회() {
        ExtractableResponse<Response> 지하철노선_조회_응답 =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("line")
                        .then().log().all()
                        .extract();

        지하철역_조회_검증(지하철노선_조회_응답);

        return 지하철노선_조회_응답;
    }

    private void 지하철역_조회_검증(ExtractableResponse<Response> 지하철노선_조회_응답) {
        assertThat(지하철노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
