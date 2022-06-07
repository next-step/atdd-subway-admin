package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanUp;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private StationResponse 강남역;
    private StationResponse 광교중앙역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setup() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        databaseCleanUp.execute();

        강남역 = StationAcceptanceTest.createStation("강남역").as(StationResponse.class);
        광교중앙역 = StationAcceptanceTest.createStation("광교중앙역").as(StationResponse.class);

        신분당선 = LineAcceptanceTest.createLine("신분당선", "bg-red-600", 강남역.getId(), 광교중앙역.getId(), 20).as(LineResponse.class);
    }
    
    @Test
    void 역_사이_새로운_역_등록() {
        StationResponse 양재역 = StationAcceptanceTest.createStation("양재역").as(StationResponse.class);
        //when 지하철 노선의 구간을 생성하면
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);

        //then : 응답 결과에서 sections에서 새로운 역의 이름을 찾을 수 있다.
        List<String> downStations = response.jsonPath().getList("sections.downStationName");

        assertThat(downStations).contains("양재역");
    }

    @Test
    void 새로운_역을_상행_종점으로_등록() {
        //when 새로운 역을 상행 종점으로 등록하면
        StationResponse 정자역 = StationAcceptanceTest.createStation("정자역").as(StationResponse.class);
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 정자역.getId(), 강남역.getId(), 7);

        //then 응답 결과에서 새롭게 등록한 상행 종점 역을 찾을 수 있고 해당 상행 종점 역은 첫번째로 위치한다.
        List<String> upStationNames = response.jsonPath().getList("sections.upStationName");

        assertThat(upStationNames).contains("정자역");
        assertThat(upStationNames.get(0)).isEqualTo("정자역");

    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {
        //when 새로운 역을 하행 종점으로 등록하면
        StationResponse 광교역 = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 광교중앙역.getId(), 광교역.getId(), 8);

        //then 응답 결과에서 새롭게 등록한 상행 종점 역을 찾을 수 있고 해당 상행 종점 역은 마지막에 위치한다.
        List<String> downStationNames = response.jsonPath().getList("sections.downStationName");

        assertThat(downStationNames).contains("광교역");
        assertThat(downStationNames.get(Math.max(downStationNames.size() - 1, 0))).isEqualTo("광교역");

    }

    @Test
    void 구간_등록시_기존_역_사이_길이보다_크거나_같은_경우_에러() {
        //when : 지하철 노선의 구간을 생성할때 거리를 라인의 거리와 같은 거리로 생성하면
        StationResponse 판교역 = StationAcceptanceTest.createStation("판교역").as(StationResponse.class);
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 20);

        //then : Bad_Request 에러가 발생한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간_등록시_상행역과_하행역이_이미_노선에_등록된_경우_에러() {
        //when 구간 등록시 상행역과 하행역이 이미 노선에 등록되어 있으면
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 광교중앙역.getId(), 8);

        //then : Bad_Request 에러가 발생한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간_등록시_상행역과_하행역_모두_포함되어_있지_않은_경우_에러() {
        //given 신분당선에 등록되어 있지않은 2개의 역을 만들고
        StationResponse 정자역 = StationAcceptanceTest.createStation("정자역").as(StationResponse.class);
        StationResponse 판교역 = StationAcceptanceTest.createStation("판교역").as(StationResponse.class);

        //when 2개의 역을 각각 upstation, downstation으로 구간 등록을 하면
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 정자역.getId(), 판교역.getId(), 10);

        //then : Bad_Request 에러가 발생한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 중간_역_제거() {
        //given 라인의 역 사이에 새로운 역을 등록하고
        StationResponse 양재역 = StationAcceptanceTest.createStation("양재역").as(StationResponse.class);
        ExtractableResponse<Response> addSectionLine = createSection(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);
        List<Integer> addAfterDistance = addSectionLine.jsonPath().get("sections.distance");

        //when 새로운 역을 삭제하면
        deleteSection(신분당선.getId(), 양재역.getId());

        //then 라인의 노선 목록 조회 시 새로운 역을 찾을 수 없고 거리는 새로운 중간역을 등록한 후 두 구간의 거리의 합이다.
        ExtractableResponse<Response> deletedSectionLine = LineAcceptanceTest.fetchLineById(신분당선.getId());
        int distance = deletedSectionLine.jsonPath().get("sections[0].distance");
        List<String> upStationNames = deletedSectionLine.jsonPath().getList("sections.upStationName");
        List<String> downStationNames = deletedSectionLine.jsonPath().getList("sections.downStationName");
        assertAll(
                () -> assertThat(!upStationNames.contains("양재역")).isTrue(),
                () -> assertThat(!downStationNames.contains("양재역")).isTrue(),
                () -> assertThat(distance).isEqualTo(addAfterDistance.stream().mapToInt(Integer::intValue).sum())
        );

    }

    @Test
    void 상행_종점역_제거() {
        //given 새로운 역을 상행 종점으로 등록하고
        StationResponse 정자역 = StationAcceptanceTest.createStation("정자역").as(StationResponse.class);
        ExtractableResponse<Response> addSectionResponse = createSection(신분당선.getId(), 정자역.getId(), 강남역.getId(), 7);
        //새로운 상행역 다음으로 오는 역
        String upStationNextStationName = addSectionResponse.jsonPath().get("sections[0].downStationName");

        //when 새로 등록된 상행 종점을 삭제하면
        deleteSection(신분당선.getId(), 정자역.getId());

        //then then 라인의 노선 목록 조회 시 새로운 상행역 다음으로 오던 역이 상행역이 된다.
        ExtractableResponse<Response> deletedSectionLine = LineAcceptanceTest.fetchLineById(신분당선.getId());
        String newUpStationName = deletedSectionLine.jsonPath().get("sections[0].upStationName");

        assertThat(newUpStationName).isEqualTo(upStationNextStationName);
    }

    @Test
    void 하행_종점역_제거() {
        //given 새로운 역을 하행 종점으로 등록하고
        StationResponse 광교역 = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);
        ExtractableResponse<Response> addSectionResponse = createSection(신분당선.getId(), 광교중앙역.getId(), 광교역.getId(), 8);
        //새로운 하행역 전에 오던 역
        String upStationOfNewDownStation = addSectionResponse.jsonPath().get("sections[1].upStationName");

        //when 새로 등록된 하행 종점을 삭제하면
        deleteSection(신분당선.getId(), 광교역.getId());

        //then 라인의 노선 목록 조회 시 새로운 하행역 전에 오던 역이 하행역이 된다.
        ExtractableResponse<Response> deletedSectionLine = LineAcceptanceTest.fetchLineById(신분당선.getId());
        String downStationName = deletedSectionLine.jsonPath().get("sections[0].downStationName");

        assertThat(downStationName).isEqualTo(upStationOfNewDownStation);

    }

    @Test
    void 구간이_하나인_노선에서_상행역_제거시_에러() {
        //when 구간이 하나인 노선에서 상행역을 제거할 시
        ExtractableResponse<Response> response = deleteSection(신분당선.getId(), 강남역.getId());
        //then 에러가 발생한다. (400 err)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간이_하나인_노선에서_하행역_제거시_에러() {
        //when 구간이 하나인 노선에서 하행역을 제거할 시
        ExtractableResponse<Response> response = deleteSection(신분당선.getId(), 광교중앙역.getId());
        //then 에러가 발생한다. (400 err)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 노선에_등록되지_않은_역_제거시_에러() {
        //when 구간에 등록되지 않은 역을 제거할 시
        StationResponse 대림역 = StationAcceptanceTest.createStation("대림역").as(StationResponse.class);
        ExtractableResponse<Response> response = deleteSection(신분당선.getId(), 대림역.getId());
        //then 에러가 발생한다. (400 err)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
       Map<String, Object> pathParams = new HashMap<>();
       pathParams.put("id", lineId);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return post("/lines/{id}/sections", pathParams, params);
    }

    private static ExtractableResponse<Response> post(String path,Map<String, ?> pathParams, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathParams)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> delete(String path, Map<String, ?> pathParams, Map<String, ?> queryParams) {
        return RestAssured.given().log().all()
                .queryParams(queryParams)
                .when().delete(path, pathParams)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("stationId", stationId);

        return delete("/{lineId}/sections", pathParams, queryParams);
    }
}
