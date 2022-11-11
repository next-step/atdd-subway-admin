package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    private final String STATION_URI = "/stations";
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseCleanup databaseCleanup;

    public ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(STATION_URI)
            .then().log().all()
            .extract();
    }

    public ExtractableResponse<Response> 지하철역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    public List<String> 지하철역_이름_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get(STATION_URI)
            .then().log().all()
            .extract().jsonPath().getList("name", String.class);

    }

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        
        databaseCleanup.execute();
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
        StationRequest 강남역_생성_요청_값 = new StationRequest("강남역");

        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_생성_요청_값);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> 지하철역_이름_목록 = 지하철역_이름_목록_조회_요청();
        assertThat(지하철역_이름_목록).containsAnyOf("강남역");
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
        StationRequest 강남역_생성_요청_값 = new StationRequest("강남역");

        지하철역_생성_요청(강남역_생성_요청_값);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_생성_요청_값);

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
        //given
        StationRequest 강남역_생성_요청_값 = new StationRequest("강남역");
        지하철역_생성_요청(강남역_생성_요청_값);

        StationRequest 판교역_생성_요청_값 = new StationRequest("판교역");
        지하철역_생성_요청(판교역_생성_요청_값);

        //when, then
        List<String> 지하철역_이름_목록 = 지하철역_이름_목록_조회_요청();
        assertThat(지하철역_이름_목록).hasSize(2);
        assertThat(지하철역_이름_목록).containsExactly("강남역", "판교역");
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
        StationRequest 강남역_생성_요청_값 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역_생성_요청_값);

        // when
        String uri = createResponse.header("Location");
        지하철역_삭제_요청(uri);
        //then
        List<String> 지하철역_이름_목록 = 지하철역_이름_목록_조회_요청();
        assertAll(
            () -> assertThat(지하철역_이름_목록).hasSize(0),
            () -> assertThat(지하철역_이름_목록.isEmpty()).isTrue()
        );
    }
}
