package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceTestAssertions.지하철역_생성_성공함;
import static nextstep.subway.station.StationAcceptanceTestAssertions.지하철역_생성_실패함;
import static nextstep.subway.station.StationAcceptanceTestAssertions.지하철역_존재하지_않음;
import static nextstep.subway.station.StationAcceptanceTestAssertions.지하철역_존재함;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_목록_조회;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_삭제;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_요청_파라미터;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @ParameterizedTest
    @ValueSource(strings = "광화문역")
    void 지하철역을_생성한다(String 지하철역_이름) {
        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(지하철역_이름);

        // then
        지하철역_생성_성공함(지하철역_생성_응답);

        // then
        List<String> 지하철역 = 지하철역_목록_조회();

        지하철역_존재함(지하철역, 지하철역_이름);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @ParameterizedTest
    @ValueSource(strings = "여의도역")
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다(String 지하철역_이름) {
        // given
        Map<String, String> 지하철역_생성_파라미터 = 지하철역_요청_파라미터(지하철역_이름);
        지하철역_생성(지하철역_생성_파라미터);

        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(지하철역_생성_파라미터);

        // then
        지하철역_생성_실패함(지하철역_생성_응답);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @ParameterizedTest
    @CsvSource({"강남역,가양역"})
    void 지하철역을_조회한다(String 지하철역1, String 지하철역2) {
        // given
        지하철역_생성(지하철역1);
        지하철역_생성(지하철역2);

        // when
        List<String> 지하철역_목록 = 지하철역_목록_조회();

        // then
        지하철역_존재함(지하철역_목록, "강남역", "가양역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 해당 지하철역을 삭제하면
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @ParameterizedTest
    @ValueSource(strings = "강남역")
    void 지하철역을_제거한다(String 지하철역_이름) {
        // given
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(지하철역_이름);

        // when
        Long 지하철역_식별자_아이디 = 지하철역_식별자(지하철역_생성_응답);
        지하철역_삭제(지하철역_식별자_아이디);

        // then
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        지하철역_존재하지_않음(지하철역_목록, 지하철역_이름);
    }

}
