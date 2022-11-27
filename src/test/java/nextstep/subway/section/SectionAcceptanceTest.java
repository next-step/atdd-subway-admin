package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static Long lineId;
    private static Long upStationId;
    private static Long downStationId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        upStationId = StationAcceptanceTest.createStationAndGetId("강남역");
        downStationId = StationAcceptanceTest.createStationAndGetId("광교역");

        lineId = LineAcceptanceTest.createLineAndGetId(
                new LineRequest("신분당선","bg-red-600",upStationId,downStationId,10));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("노선에 구간을 등록한다. 상위역을 새로운역으로 등록")
    void addSectionUpStationTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");

        //when
        final ExtractableResponse<Response> apiResponse = createSection(lineId,
                new SectionRequest(newStationId, downStationId, 1));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(apiResponse.jsonPath().getList("sectionResponses.upStation.id",Long.class))
                .containsExactly(upStationId,newStationId);
        assertThat(apiResponse.jsonPath().getList("sectionResponses.downStation.id",Long.class))
                .containsExactly(newStationId,downStationId);
    }

    @Test
    @DisplayName("노선에 구간을 등록한다. 하위역을 새로운역으로 등록")
    void addSectionDownStationTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");

        //when
        final ExtractableResponse<Response> apiResponse = createSection(lineId,
                new SectionRequest(upStationId, newStationId, 1));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(apiResponse.jsonPath().getList("sectionResponses.upStation.id",Long.class))
                .containsExactly(upStationId,newStationId);
        assertThat(apiResponse.jsonPath().getList("sectionResponses.downStation.id",Long.class))
                .containsExactly(newStationId,downStationId);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록")
    void addNewStationFirstUpStation() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("새로운상행종점");

        //when
        final ExtractableResponse<Response> apiResponse = createSection(lineId,
                new SectionRequest(newStationId, upStationId, 1));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(apiResponse.jsonPath().getList("sectionResponses.upStation.id",Long.class))
                .containsExactly(newStationId);
        assertThat(apiResponse.jsonPath().getList("sectionResponses.downStation.id",Long.class))
                .containsExactly(newStationId,upStationId);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록")
    void addNewStationLastDownStation() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("새로운하행종점");

        //when
        final ExtractableResponse<Response> apiResponse = createSection(lineId,
                new SectionRequest(downStationId, newStationId, 1));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(apiResponse.jsonPath().getList("sectionResponses.upStation.id",Long.class))
                .containsExactly(downStationId, newStationId);
        assertThat(apiResponse.jsonPath().getList("sectionResponses.downStation.id",Long.class))
                .containsExactly(newStationId);
    }

    @Test
    @DisplayName("가운데 역을 제거 한다.")
    void deleteMiddleStationTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");
        createSection(lineId,
                new SectionRequest(newStationId, downStationId, 1));

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, newStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(LineAcceptanceTest.retrieveLineResponse(lineId).getStations()
                .stream().map(StationResponse::getId)).doesNotContain(newStationId);
    }

    @Test
    @DisplayName("상행종점을 제거 한다.")
    void deleteFirstStationTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");
        createSection(lineId,
                new SectionRequest(newStationId, downStationId, 1));

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, upStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(LineAcceptanceTest.retrieveLineResponse(lineId).getStations()
                .stream().map(StationResponse::getId))
                .doesNotContain(upStationId);
    }

    @Test
    @DisplayName("하행종점을 제거 한다.")
    void deleteLastStationTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");
        createSection(lineId,
                new SectionRequest(newStationId, downStationId, 1));

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, downStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(LineAcceptanceTest.retrieveLineResponse(lineId).getStations()
                .stream().map(StationResponse::getId))
                .doesNotContain(downStationId);
    }

    @Test
    @DisplayName("구간이 하나인 경우 상행역을 제거하면 오류가 발생한다.")
    void deleteUpStationOnlyOneSectionExceptionTest() {
        //given

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, upStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("구간이 하나인 경우 하행역을 제거하면 오류가 발생한다.")
    void deleteDownStationOnlyOneSectionExceptionTest() {
        //given

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, downStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("호선에 존재하지 않는 역을 제거하면 오류가 발생한다.")
    void deleteNotExistStationExceptionTest() {
        //given
        Long newStationId = StationAcceptanceTest.createStationAndGetId("양재역");

        //when
        final ExtractableResponse<Response> apiResponse = deleteStation(lineId, newStationId);

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * 구간생성 api 호출
     * */
    ExtractableResponse<Response> createSection(Long lineId, SectionRequest sectionRequest) {
        final Map param = new HashMap();
        param.put("upStationId", sectionRequest.getUpStationId());
        param.put("downStationId", sectionRequest.getDownStationId());
        param.put("distance", sectionRequest.getDistance());

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }

    /**
     * 구간삭제 api 호출
     */
    ExtractableResponse<Response> deleteStation(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all().extract();
    }
}
