package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class SectionAcceptanceTest extends AcceptanceTest {
    StationResponse 강남역;
    StationResponse 광교역;
    StationResponse 양재역;
    StationResponse 판교역;
    Map<String, String> createParams;
    LineResponse 신분당선;

    public static ExtractableResponse<Response> requestSectionCreation(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id.toString() + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestSectionDelete(Long id, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam("stationId", stationId)
                .delete("/lines/" + id.toString() + "/sections")
                .then().log().all()
                .extract();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.makeStation("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.makeStation("광교역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.makeStation("양재역").as(StationResponse.class);
        판교역 = StationAcceptanceTest.makeStation("판교역").as(StationResponse.class);

        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 10 + "");
        신분당선 = LineAcceptanceTest.requestLineCreation(createParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 노선에_구간을_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("downStationId", 강남역.getId() + "");
        createParams.put("upStationId", 판교역.getId() + "");
        createParams.put("distance", 8 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        // then
        // 지하철_노선에_구간_등록됨
        assertResponseStatusAndLocation(response);
    }

    @DisplayName("역 사이에 상행역이 동일한 새로운 역을 등록한다.")
    @Test
    void 역_사이에_상행역이_동일한_새로운_역을_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 판교역.getId() + "");
        createParams.put("distance", 8 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        ExtractableResponse<Response> sectionsResponse = requestReadURI("/lines/" + 신분당선.getId() + "/sections");
        ExtractableResponse<Response> lineResponse = requestReadURI("/lines/" + 신분당선.getId());
        List<Integer> sectionDistances = getSection(sectionsResponse);
        List<String> stations = getStations(lineResponse);

        // then 구간 길이별로 나눠짐
        assertResponseStatusAndLocation(response);
        assertThat(sectionDistances).containsAll(Arrays.asList(2, 8));
        assertThat(stations).containsExactlyElementsOf(Arrays.asList("강남역", "판교역", "광교역"));
    }

    @DisplayName("역 사이에 상행역이 동일한 새로운 역을 등록한다.")
    @Test
    void 역_사이에_하행역이_동일한_새로운_역을_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 판교역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 7 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);
        ExtractableResponse<Response> lineResponse = requestReadURI("/lines/" + 신분당선.getId());

        ExtractableResponse<Response> sectionsResponse = requestReadURI("/lines/" + 신분당선.getId() + "/sections");
        List<Integer> sectionDistances = getSection(sectionsResponse);
        List<String> stations = getStations(lineResponse);

        // then 구간 길이별로 나눠짐
        assertResponseStatusAndLocation(response);
        assertThat(sectionDistances).containsAll(Arrays.asList(3, 7));
        assertThat(stations).containsExactlyElementsOf(Arrays.asList("강남역", "판교역", "광교역"));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void 새로운_역을_상행_종점으로_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 양재역.getId() + "");
        createParams.put("downStationId", 강남역.getId() + "");
        createParams.put("distance", 7 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);
        ExtractableResponse<Response> lineResponse = requestReadURI("/lines/" + 신분당선.getId());

        ExtractableResponse<Response> sectionsResponse = requestReadURI("/lines/" + 신분당선.getId() + "/sections");
        List<Integer> sectionDistances = getSection(sectionsResponse);
        List<String> stations = getStations(lineResponse);

        // then 구간 길이별로 나눠짐
        assertResponseStatusAndLocation(response);
        assertThat(sectionDistances).containsAll(Arrays.asList(10, 7));
        assertThat(stations).containsExactlyElementsOf(Arrays.asList("양재역", "강남역", "광교역"));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void 새로운_역을_하행_종점으로_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 광교역.getId() + "");
        createParams.put("downStationId", 양재역.getId() + "");
        createParams.put("distance", 7 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);
        ExtractableResponse<Response> lineResponse = requestReadURI("/lines/" + 신분당선.getId());

        ExtractableResponse<Response> sectionsResponse = requestReadURI("/lines/" + 신분당선.getId() + "/sections");
        List<Integer> sectionDistances = getSection(sectionsResponse);
        List<String> stations = getStations(lineResponse);

        // then 구간 길이별로 나눠짐
        assertResponseStatusAndLocation(response);
        assertThat(sectionDistances).containsAll(Arrays.asList(10, 7));
        assertThat(stations).containsExactlyElementsOf(Arrays.asList("강남역", "광교역", "양재역"));
    }

    @DisplayName("새로운 구간의 길이가 기존 구간 사이 길이보다 크거나 같다")
    @Test
    void 새로운_구간의_길이가_기존_구간_사이_길이보다_크거나_같다() {
        // whe n
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 판교역.getId() + "");
        createParams.put("distance", 10 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("새로운 구간의 상행/하행역 모두 기존에 존재한다.")
    @Test
    void 새로운_구간의_상행하행역_모두_기존에_존재한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 8 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("새로운 구간의 상행/하행역 모두 기존에 부재한다.")
    @Test
    void 새로운_구간의_상행하행역_모두_기존에_부재한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 판교역.getId() + "");
        createParams.put("downStationId", 양재역.getId() + "");
        createParams.put("distance", 8 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("노선의 구간을 제거한다")
    @Test
    void 노선의_구간을_제거한다() {
        // given
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 판교역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 7 + "");
        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        // when
        // 지하철 노선에 구간 삭제 요청
        ExtractableResponse<Response> deleteResponse = requestSectionDelete(신분당선.getId(), 판교역.getId());
        ExtractableResponse<Response> lineResponse = requestReadURI("/lines/" + 신분당선.getId());

        ExtractableResponse<Response> sectionsResponse = requestReadURI("/lines/" + 신분당선.getId() + "/sections");
        List<Integer> sectionDistances = getSection(sectionsResponse);
        List<String> stations = getStations(lineResponse);

        // then 구간 삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(sectionDistances).containsAll(Arrays.asList(10));
        assertThat(stations).containsExactlyElementsOf(Arrays.asList("강남역", "광교역"));
    }

    private void assertResponseStatusAndLocation(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> requestReadURI(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    private List<Integer> getSection(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", SectionResponse.class).stream()
                .map(SectionResponse::getDistance)
                .collect(Collectors.toList());
    }

    private List<String> getStations(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
