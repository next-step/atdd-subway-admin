package nextstep.subway.station;

import static nextstep.subway.AcceptanceTestFactory.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import nextstep.subway.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.cleanUp();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 지하철_역_생성_응답_결과 = 지하철_역_생성("강남역");
        // then
        생성_성공_확인(지하철_역_생성_응답_결과);
        // then
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        목록_조회_성공_확인(지하철_역_이름_목록, "강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_생성("강남역");
        // when
        ExtractableResponse<Response> 지하철_역_생성_요청_결과 = 지하철_역_생성("강남역");
        // then
        생성_실패_확인(지하철_역_생성_요청_결과);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        지하철_역_생성("공덕역");
        지하철_역_생성("애오개역");
        //when
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        //then
        목록_조회_성공_확인(지하철_역_이름_목록, "공덕역", "애오개역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        Long 지하철_역_ID = 지하철_역_생성_ID_추출("공덕역");
        //when
        지하철_역_삭제_요청(지하철_역_ID);
        //then
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        목록_조회_실패_확인(지하철_역_이름_목록, "공덕역");
    }
}
