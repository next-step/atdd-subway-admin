package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.*;
import nextstep.subway.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철구간 관련 기능")
@ActiveProfiles(value = "acceptance")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner cleaner;

    StationResponse gangnam;
    StationResponse gyodae;
    LineResponse shinbundang;

    @BeforeEach
    public void beforeEach() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        createStations();
        createLine();
    }

    @AfterEach
    public void afterEach() {
        cleaner.execute();
    }

    void createStations() {
        gangnam = createStation(new StationRequest("강남역")).as(StationResponse.class);
        gyodae = createStation(new StationRequest("교대역")).as(StationResponse.class);
    }

    void createLine() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L);
        shinbundang = createLine(request).as(LineResponse.class);
    }

    @DisplayName("노선 중간에 구간을 등록한다.")
    @Test
    void addSectionBetweenStations() {
        // when
        StationResponse sinchon = createStation(new StationRequest("신촌역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(gangnam.getId(), sinchon.getId(), 1L));

        // then
        assertThat(getLine(shinbundang.getId()).as(LineResponse[].class)[0].getStations()).hasSize(3);
    }

    @DisplayName("노선 시작에 구간을 등록한다.")
    @Test
    void addSectionAtBegin() {
        // when
        StationResponse sinchon = createStation(new StationRequest("신촌역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(sinchon.getId(), gangnam.getId(), 1L));

        // then
        assertThat(getLine(shinbundang.getId()).as(LineResponse[].class)[0].getStations()).hasSize(3);
    }

    @DisplayName("노선 마지막에 구간을 등록한다.")
    @Test
    void addSectionAtEnd() {
        // when
        StationResponse sinchon = createStation(new StationRequest("신촌역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(gyodae.getId(), sinchon.getId(), 1L));

        // then
        assertThat(getLine(shinbundang.getId()).as(LineResponse[].class)[0].getStations()).hasSize(3);
    }

    @DisplayName("구간 내에 존재하는 출발역을 삭제한다.")
    @Test
    void removeUpStation() {
        // given
        StationResponse sinchon = createStation(new StationRequest("신촌역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(gyodae.getId(), sinchon.getId(), 1L));
        StationResponse sadang = createStation(new StationRequest("사당역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(sinchon.getId(), sadang.getId(), 1L));

        // when
        removeStation(shinbundang.getId(), gyodae.getId());

        // then
        checkStationOrder(getStationNames(), Arrays.asList("강남역", "교대역", "사당역"));
    }

    @DisplayName("구간 내에 존재하는 도착역을 삭제한다.")
    @Test
    void removeDownStation() {
        // given
        StationResponse sinchon = createStation(new StationRequest("신촌역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(gyodae.getId(), sinchon.getId(), 1L));
        StationResponse sadang = createStation(new StationRequest("사당역")).as(StationResponse.class);
        createSection(shinbundang.getId(), new SectionRequest(sinchon.getId(), sadang.getId(), 1L));

        // when
        removeStation(shinbundang.getId(), sinchon.getId());

        // then
        checkStationOrder(getStationNames(), Arrays.asList("강남역", "교대역", "사당역"));
    }

    ExtractableResponse<Response> createStation(StationRequest request) {
        return RestAssured.given().log().all()
                          .body(request)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/stations")
                          .then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> createLine(LineRequest request) {
        return RestAssured.given().log().all()
                          .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines").then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
                          .when().queryParam("id", id).get("/lines")
                          .then().log().all()
                          .extract();
    }

    void createSection(Long id, SectionRequest request) {
        RestAssured.given().log().all()
                   .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().post("/{lineId}/sections", id)
                   .then().log().all()
                   .extract();
    }

    void removeStation(Long lineId, Long stationId) {
        Map<String, Long> param = new HashMap<>();
        param.put("stationId", stationId);

        RestAssured.given().queryParams(param).log().all()
                   .when().delete("/{id}/sections", lineId)
                   .then().log().all()
                   .extract();
    }

    List<String> getStationNames() {
        return RestAssured.given().log().all()
                          .when().get("/stations")
                          .then().log().all()
                          .extract().jsonPath().getList("name", String.class);
    }

    void checkStationOrder(List<String> actual, List<String> expected) {
        assertThat(actual).containsAll(expected);
    }
}
