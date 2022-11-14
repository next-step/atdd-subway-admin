package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceTestFixture.*;
import static nextstep.subway.utils.JsonPathUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleaner;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    public static final String GANGNAM = "강남역";
    public static final String WANGSIPLI = "왕십리역";
    public static final String JUKJUN = "죽전역";
    public static final String BUNDANG = "분당역";

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
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
        ExtractableResponse<Response> response = 지하철역_생성(GANGNAM);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 역목록_이름_추출(지하철역_조회());
        assertThat(stationNames).containsAnyOf(GANGNAM);
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
        지하철역_생성(GANGNAM);

        // when
        ExtractableResponse<Response> response = 지하철역_생성(GANGNAM);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성(GANGNAM);

        // given
        지하철역_생성(BUNDANG);

        // when
        ExtractableResponse<Response> response = 지하철역_조회();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(결과_목록_추출(response)).hasSize(2),
            () -> assertThat(역목록_이름_추출(response)).containsExactly(GANGNAM, BUNDANG)
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Integer id = ID_추출(지하철역_생성(GANGNAM));

        // when
        지하철역_삭제(id);

        // then
        ExtractableResponse<Response> response = 지하철역_조회();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(결과_목록_추출(response)).hasSize(0)
        );
    }
    private Integer ID_추출(ExtractableResponse<Response> response) {
        return extractInteger(response, "$.id");
    }

    private List<Object> 결과_목록_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$");
    }

    private List<String> 역목록_이름_추출(ExtractableResponse<Response> response) {
        return extractList(response, "$[*].name");
    }
}
