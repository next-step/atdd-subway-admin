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

public class SectionAcceptanceTest extends AcceptanceTest {
    StationResponse 강남역;
    StationResponse 광교역;
    StationResponse 양재역;
    StationResponse 판교역;
    Map<String, String> createParams;
    LineResponse 신분당선;

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

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void 역_사이에_새로운_역을_등록한다() {
        // when
        // 지하철_노선에_구간_등록_요청
        createParams = new HashMap<>();
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 판교역.getId() + "");
        createParams.put("distance", 8 + "");

        ExtractableResponse<Response> response = requestSectionCreation(신분당선.getId(), createParams);

        ExtractableResponse<Response> sectionsResponse = requestReadSections("/lines/" + 신분당선.getId() + "/sections");
        List<Integer> sectionDistances = getSection(sectionsResponse);

        // then 구간 길이별로 나눠짐
        assertResponseStatusAndLocation(response);
        assertThat(sectionDistances).containsAll(Arrays.asList(2, 8));
    }

    public static ExtractableResponse<Response> requestSectionCreation(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id.toString() + "/sections")
                .then().log().all()
                .extract();
    }

    private void assertResponseStatusAndLocation(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> requestReadSections(String uri) {
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
}
