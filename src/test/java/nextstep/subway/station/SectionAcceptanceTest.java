package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static nextstep.subway.station.LineAcceptanceTest.구간_추가;
import static nextstep.subway.station.LineAcceptanceTest.노선_등록;
import static nextstep.subway.station.StationAcceptanceTest.응답_객체_생성;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse A역;
    private StationResponse B역;
    private StationResponse C역;
    private StationResponse D역;
    private StationResponse E역;
    private StationResponse F역;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();

        A역 = 응답_객체_생성(지하철역_등록("A역"), StationResponse.class);
        B역 = 응답_객체_생성(지하철역_등록("B역"), StationResponse.class);
        C역 = 응답_객체_생성(지하철역_등록("C역"), StationResponse.class);
        D역 = 응답_객체_생성(지하철역_등록("D역"), StationResponse.class);
        E역 = 응답_객체_생성(지하철역_등록("E역"), StationResponse.class);
        F역 = 응답_객체_생성(지하철역_등록("F역"), StationResponse.class);
    }

    /**
     * Given 노선 및 구간을 설정한다.
     * When, then 노선 내 순서대로 구간을 조회한다.
     */
    @DisplayName("노선에 포함된 구간 리스트 조회")
    @Test
    void showLineSections() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), D역.getId()), LineResponse.class);
        구간_추가(A역.getId(), B역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(E역.getId(), A역.getId(), 15, line.getId());
        구간_추가(D역.getId(), F역.getId(), 30, line.getId());

        // when, then
        노선_구간_조회(line.getId());
    }

    public static ValidatableResponse 노선_구간_조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/sections/" + lineId)
                .then().log().all();
    }
}
