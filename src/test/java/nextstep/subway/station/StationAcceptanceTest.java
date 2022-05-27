package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.station.StationRequest.지하철_삭제_요청;
import static nextstep.subway.station.StationRequest.지하철역_생성_요청;
import static nextstep.subway.station.StationRequest.지하철역_조회_요청;
import static nextstep.subway.station.StationRequest.지하철역_존재;
import static nextstep.subway.station.StationResponse.지하철역_2개_조회_응답_성공;
import static nextstep.subway.station.StationResponse.지하철역_삭제_성공;
import static nextstep.subway.station.StationResponse.지하철역_생성_성공;
import static nextstep.subway.station.StationResponse.지하철역_생성_요청_실패;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private static final String 강남역 = "강남역";
    private static final String 역삼역 = "역삼역";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철역_생성하고_조회한다() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성_성공(response);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void 존재하는_지하철역은_생성못한다() {
        // given
        지하철역_존재(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성_요청_실패(response);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void 지하철역_2개_생성하고_조회한다() {
        // given
        지하철역_존재(강남역);
        지하철역_존재(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_조회_요청();

        // then
        지하철역_2개_조회_응답_성공(response);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void 지하철역_삭제한다() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_삭제_요청(1L);

        // then
        지하철역_삭제_성공(response);
    }
}
