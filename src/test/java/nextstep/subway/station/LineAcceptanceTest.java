package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_성공_id_응답;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    private static final int FIRST_STATION_INDEX = 0;
    private static final int SECOND_STATION_INDEX = 1;
    private static final int THIRD_STATION_INDEX = 2;

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanUp databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When: 지하철 노선을 생성하면
     * Then: 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 노선_생성() {

        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // when:
        // 노선 생성
        Map<String, Object> lineParams = 노선_생성_요청파라미터("line", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> response = 노선_생성_성공(lineParams);

        //then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given: 지하철 노선들을 생성하고
     * When:  지하철 노선들을 조회하면
     * Then:  생성한 노선들을 찾을 수 있다
     */
    @DisplayName("지하철 노선 목록을 가져온다")
    @Test
    void 노선_목록() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선, 경인선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        Map<String, Object> kyunginLine = 노선_생성_요청파라미터("kyunginLine", "bg-green-600", 삼전역_id, 강남역_id);
        LineResponse boondangResponse = 노선_생성_성공(boondangLine).body().jsonPath().getObject("", LineResponse.class);
        LineResponse kyunginResponse = 노선_생성_성공(kyunginLine).body().jsonPath().getObject("", LineResponse.class);

        // when:
        // 노선 목록 조회
        ExtractableResponse<Response> response = 노선_조회("/lines");

        List<Long> lineIds = response.body().jsonPath().getList("id", Long.class);

        // then:
        assertThat(lineIds).containsAnyOf(boondangResponse.getId(), kyunginResponse.getId());
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 조회하면
     * Then:  생성한 지하철 노선을 찾을 수 있다
     */
    @DisplayName("단건 지하철 노선을 가져온다")
    @Test
    void 노선_단건_조회() {

        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        // when:
        // 분당선 조회
        ExtractableResponse<Response> response = 노선_조회(boondangGetUrl);
        Long lineId = response.body().jsonPath().getLong("id");

        // then:
        assertThat(lineId).isEqualTo(createResponse.body().jsonPath().getLong("id"));
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 수정하면
     * Then:  지하철 노선의 수정을 확인할 수 있다.
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void 지하철_노선_수정() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        // when:
        // 분당선 수정
        Map<String, String> updateRequest = new HashMap<>();
        String updateName = "다른라인";
        String updateColor = "bg-blue-600";
        updateRequest.put("name", updateName);
        updateRequest.put("color", updateColor);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when().put(boondangGetUrl)
                .then().log().all()
                .extract();

        // 수정 된 분당선 조회
        ExtractableResponse<Response> updatedLine = 노선_조회(boondangGetUrl);
        String updatedName = updatedLine.body().jsonPath().get("name");
        String updatedColor = updatedLine.body().jsonPath().get("color");

        assertThat(updatedName).isEqualTo(updateName);
        assertThat(updatedColor).isEqualTo(updateColor);
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 삭제하면
     * Then:  해당 지하철 노선은 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void 지하철_노선_삭제() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        // 분당선 삭제
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete(boondangGetUrl)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  새로운 상행 종점을 포함하는 구간을 노선에 등록하면
     * then:  노선의 새로운 상행 종점 구간이 등록 된다
     */
    @DisplayName("상행 종점 등록 성공")
    @Test
    void 상행_종점_등록_성공() {
        // given:
        // 삼전역, 강남역, 석촌고분역, 인천공항역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");
        long 인천공항역_id = 지하철역_생성("인천공항역");
        int existDistance = 10;
        int newDistance = 10;

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id, existDistance);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id, existDistance));

        // when:
        // 신규 상행 종점 등록
        // *인천공항역* -> 삼전역 -> 강남역 -> 석촌고분역
        ExtractableResponse<Response> response = 노선_구간_생성(lineId, 구간_생성(인천공항역_id, 삼전역_id, newDistance));
        // 총 구간 길이
        int totalDistance = existDistance + existDistance + newDistance;
        // 신규 상행 종점 구간
        Map<String, Integer> firstStation = getResultSection(response, FIRST_STATION_INDEX);
        // 초기 생성 구간
        Map<String, Integer> secondStation = getResultSection(response, SECOND_STATION_INDEX);

        // then:
        assertThat(firstStation.get("preStationId")).isEqualTo(인천공항역_id);
        assertThat(firstStation.get("nextStationId")).isEqualTo(삼전역_id);
        assertThat(secondStation.get("preStationId")).isEqualTo(삼전역_id);
        assertThat(secondStation.get("nextStationId")).isEqualTo(강남역_id);
        assertThat(totalDistance).isEqualTo(getLineDistance(response));
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  새로운 하행 종점을 포함하는 구간을 노선에 등록하면
     * then:  노선의 새로운 하행 종점 구간이 등록 된다
     */
    @DisplayName("하행 종점 등록 성공")
    @Test
    void 하행_종점_등록_성공() {
        // given:
        // 삼전역, 강남역, 석촌고분역, 인천공항역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");
        long 인천공항역_id = 지하철역_생성("인천공항역");
        int existDistance = 10;
        int newDistance = 10;

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id, existDistance);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id, existDistance));

        // when:
        // 신규 하행 종점 등록
        // 삼전역 -> 강남역 -> 석촌고분역 -> *인천공항역*
        ExtractableResponse<Response> response = 노선_구간_생성(lineId, 구간_생성(석촌고분역_id, 인천공항역_id, newDistance));
        // 총 구간 길이
        int totalDistance = existDistance + existDistance + newDistance;
        // 기존 하행 종점 구간
        Map<String, Integer> secondSection = getResultSection(response, SECOND_STATION_INDEX);
        // 하행 상행 종점 구간
        Map<String, Integer> newSection = getResultSection(response, THIRD_STATION_INDEX);

        // then:
        assertThat(secondSection.get("preStationId")).isEqualTo(강남역_id);
        assertThat(secondSection.get("nextStationId")).isEqualTo(석촌고분역_id);
        assertThat(newSection.get("preStationId")).isEqualTo(석촌고분역_id);
        assertThat(newSection.get("nextStationId")).isEqualTo(인천공항역_id);
        assertThat(totalDistance).isEqualTo(getLineDistance(response));
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  기존 상행 종점을 기준으로 하는 노선 구간을 등록하면
     * then:  노선의 새로운 구간이 등록된다
     */
    @DisplayName("상행 구간 등록 성공")
    @Test
    void 상행_구간_등록_성공() {
        // given:
        // 삼전역, 강남역, 석촌고분역, 인천공항역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");
        long 인천공항역_id = 지하철역_생성("인천공항역");
        int existDistance = 10;
        int 신규_구간_길이 = 7;

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id, existDistance);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id, existDistance));

        // when:
        // 신규 상행 구간 등록
        // 삼전역 -> *인천공항역* -> 강남역 -> 석촌고분역
        ExtractableResponse<Response> response = 노선_구간_생성(lineId, 구간_생성(삼전역_id, 인천공항역_id, 신규_구간_길이));
        // 총 구간 길이
        int totalDistance = getLineDistance(response);
        // 신규 상행 구간
        Map<String, Integer> firstSection = getResultSection(response, FIRST_STATION_INDEX);
        // 기존 상행 구간
        Map<String, Integer> secondSection = getResultSection(response, SECOND_STATION_INDEX);

        // then:
        assertThat(firstSection.get("preStationId")).isEqualTo(삼전역_id);
        assertThat(firstSection.get("nextStationId")).isEqualTo(인천공항역_id);
        assertThat(secondSection.get("preStationId")).isEqualTo(인천공항역_id);
        assertThat(secondSection.get("nextStationId")).isEqualTo(강남역_id);
        assertThat(firstSection.get("distance")).isEqualTo(신규_구간_길이);
        assertThat(secondSection.get("distance")).isEqualTo(totalDistance - existDistance - 신규_구간_길이);
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  기존 하행 종점을 기준으로 하는 노선 구간을 등록하면
     * then:  노선의 새로운 구간이 등록된다
     */
    @DisplayName("하행 구간 등록 성공")
    @Test
    void 하행_구간_등록_성공() {
        // given:
        // 삼전역, 강남역, 석촌고분역, 인천공항역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");
        long 인천공항역_id = 지하철역_생성("인천공항역");
        int existDistance = 10;
        int 신규_구간_길이 = 7;

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id, existDistance);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id, existDistance));

        // when:
        // 하행 구간 등록
        // 삼전역 -> *인천공항역* -> 강남역 -> 석촌고분역
        ExtractableResponse<Response> response = 노선_구간_생성(lineId, 구간_생성(인천공항역_id, 강남역_id, 신규_구간_길이));
        // 총 구간 길이
        int totalDistance = getLineDistance(response);
        // 초기 하행 구간
        Map<String, Integer> firstSection = getResultSection(response, FIRST_STATION_INDEX);
        // 신규 하행 구간
        Map<String, Integer> secondSection = getResultSection(response, SECOND_STATION_INDEX);

        // then:
        assertThat(firstSection.get("preStationId")).isEqualTo(삼전역_id);
        assertThat(firstSection.get("nextStationId")).isEqualTo(인천공항역_id);
        assertThat(secondSection.get("preStationId")).isEqualTo(인천공항역_id);
        assertThat(secondSection.get("nextStationId")).isEqualTo(강남역_id);
        assertThat(firstSection.get("distance")).isEqualTo(totalDistance - existDistance - 신규_구간_길이);
        assertThat(secondSection.get("distance")).isEqualTo(신규_구간_길이);
    }

    /**
     * given: 두 개의 지하철 역을 생성하고
     * given: 생성한 두 개의 지하철 역을 각 상행 종점, 하행 종점으로 포함하는 노선을 생성하고
     * when:  동일한 상행 종점, 하행 종점을 가지는 구간을 등록하면
     * then:  노선의 구간으로 등록되지 않는다
     */
    @DisplayName("구간 추가 실패 - 이미 존재하는 구간")
    @Test
    void 구간_추가_실패_이미_존재하는_구간() {
        // given:
        // 삼전역, 강남역 생성
        long 상행_종점_id = 지하철역_생성("삼전역");
        long 하행_종점_id = 지하철역_생성("강남역");

        // 분당선 생성, 및 조회
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 상행_종점_id, 하행_종점_id);

        // when:
        // 동일한 상행 종점, 하행 종점을 가지는 구간 등록
        ExtractableResponse<Response> makeLineStationResponse = 노선_구간_생성(lineId, 구간_생성(상행_종점_id, 하행_종점_id, 10));

        // then:
        assertThat(makeLineStationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given: 두 개의 지하철 역을 생성하고
     * given: 생성한 두 개의 지하철 역을 각 상행 종점, 하행 종점으로 포함하는 노선을 생성하고
     * given: 생성한 노선에 포함되지 않는 새로운 두 개의 지하철 역을 생성하고
     * when:  노선의 구간에 포함되지 않는 두 개의 지하철 역을 노선의 구간으로 등록하면
     * then:  노선의 구간으로 등록되지 않는다
     */
    @DisplayName("구간 추가 실패 - 상행역과 하행역 둘 중 하나도 포함되어 있지 않은 경우")
    @Test
    void 구간_추가_실패_상행역_하행역_모두_포함되지_않은_경우() {
        // given:
        // 삼전역, 강남역 생성
        long 상행_종점_id = 지하철역_생성("삼전역");
        long 하행_종점_id = 지하철역_생성("강남역");

        // 분당선 생성, 및 조회
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 상행_종점_id, 하행_종점_id);

        // 노선의 구간에 포함되지 않는 양평역, 춘천역 생성
        long 구간에_포함되지_않는_상행_종점_id = 지하철역_생성("양평역");
        long 구간에_포함되지_않는_하행_종점_id = 지하철역_생성("춘천역");

        // when:
        // 노선의 구간에 포함되지 않는 두 개의 지하철 역을 노선의 구간으로 등록
        ExtractableResponse<Response> makeLineStationResponse = 노선_구간_생성(lineId,
                구간_생성(구간에_포함되지_않는_상행_종점_id, 구간에_포함되지_않는_하행_종점_id, 10));

        // then:
        assertThat(makeLineStationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given: 두 개의 지하철 역을 생성하고
     * given: 생성한 두 개의 지하철 역을 각 상행 종점, 하행 종점으로 포함하는 노선을 생성하고
     * given: 구간의 길이가 동일한 구간을 생성하고
     * when:  구간의 길이가 동일한 구간을 노선의 구간으로 등록하면
     * then:  노선의 구간으로 등록되지 않는다
     */
    @DisplayName("상행 구간 등록 실패 - 기존 역 사이 길이보다 크거나 같은 경우")
    @Test
    void 상행_구간_등록_실패_기존_구간_길이보다_크거나_같은_경우() {
        // given:
        // 삼전역, 강남역, 인천공항역, 구간 거리 생성
        long 상행_종점_id = 지하철역_생성("삼전역");
        long 하행_종점_id = 지하철역_생성("강남역");
        long 신규_상행_구간_id = 지하철역_생성("인천공항역");
        int distance = 10;

        // 분당선 생성, 및 조회
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 상행_종점_id, 하행_종점_id, distance);

        // when:
        // 구간 길이가 동일한 구간 생성
        ExtractableResponse<Response> makeLineStationResponse = 노선_구간_생성(
                lineId, 구간_생성(상행_종점_id, 신규_상행_구간_id, distance));

        // then:
        assertThat(makeLineStationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  상행 종점을 삭제하면
     * then:  상행 종점이 삭제되고, 그 다음 역이 상행 종점이 된다
     */
    @DisplayName("상행 구간 종점 삭제")
    @Test
    void 상행_구간_종점_삭제() {
        // given:
        // 삼전역, 강남역, 석촌고분역
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id));

        // when:
        // 삼전역(상행 종점) 삭제
        ExtractableResponse<Response> response = 구간_삭제(lineId, 삼전역_id);

        // 신규 상행 종점
        Map<String, Integer> firstSection = getResultSection(response, FIRST_STATION_INDEX);
        // 구간 갯수
        int sectionSize = getSectionSize(response);

        // then:
        // 강남역 -> 석촌고분역
        assertThat(firstSection.get("preStationId")).isEqualTo(강남역_id);
        assertThat(firstSection.get("nextStationId")).isEqualTo(석촌고분역_id);
        assertThat(sectionSize).isEqualTo(1);
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  하행 종점을 삭제하면
     * then:  하행 종점이 삭제되고, 그 이전 역이 하행 종점이 된다
     */
    @DisplayName("하행 구간 종점 삭제")
    @Test
    void 하행_구간_종점_삭제() {
        // given:
        // 삼전역, 강남역, 석촌고분역
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id));

        // when:
        // 석촌 고분역(하행 종점) 삭제
        ExtractableResponse<Response> response = 구간_삭제(lineId, 석촌고분역_id);

        // 신규 하행 종점
        Map<String, Integer> firstSection = getResultSection(response, FIRST_STATION_INDEX);
        // 구간 갯수
        int sectionSize = getSectionSize(response);

        // then:
        // 삼전역 -> 강남역
        assertThat(firstSection.get("preStationId")).isEqualTo(삼전역_id);
        assertThat(firstSection.get("nextStationId")).isEqualTo(강남역_id);
        assertThat(sectionSize).isEqualTo(1);
    }

    /**
     * given: 두 개의 구간을 갖는 노선을 생성하고
     * when:  두 구간의 가운데 지점의 역을 삭제하면
     * then:  가운데 역이 삭제되고, 삭제 된 역 기준 상행역, 하행역이 새로운 구간이 된다
     */
    @DisplayName("가운데 역 삭제")
    @Test
    void 가운데_역_제거() {
        // given:
        // 삼전역, 강남역, 석촌고분역
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");
        int firstDistance = 10;
        int secondDistance = 10;

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역 -> 석촌고분역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id, firstDistance);
        노선_구간_생성(lineId, 구간_생성(강남역_id, 석촌고분역_id, secondDistance));

        // when:
        // 강남역(가운데역) 삭제
        ExtractableResponse<Response> response = 구간_삭제(lineId, 강남역_id);
        // 갱신 된 노선
        Map<String, Integer> firstSection = getResultSection(response, FIRST_STATION_INDEX);
        // 전체 구간 길이
        int totalDistance = getLineDistance(response);
        // 구간 갯수
        int sectionSize = getSectionSize(response);

        // then:
        // 삼전역 -> 석촌고분역
        assertThat(firstSection.get("preStationId")).isEqualTo(삼전역_id);
        assertThat(firstSection.get("nextStationId")).isEqualTo(석촌고분역_id);
        assertThat(totalDistance).isEqualTo(firstDistance + secondDistance);
        assertThat(sectionSize).isEqualTo(1);
    }

    /**
     * given: 하나의 구간을 갖는 노선을 생성하고
     * when:  구간에 포함 된 역을 제거하면
     * then:  구간이 제거되지 않는다
     */
    @DisplayName("구간이 하나인 노선에서 역을 제거하는 경우")
    @Test
    void 제거_실패_구간이_하나인_노선에서_역을_제거하는_꼉우() {
        // given:
        // 삼전역, 강남역
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);

        // when:
        // 삼전역 삭제
        ExtractableResponse<Response> response = 구간_삭제(lineId, 삼전역_id);

        // then:
        // 구간 삭제 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given: 하나의 구간을 갖는 노선을 생성하고
     * when:  구간에 포함되지 않은 역을 제거하면
     * then:  구간이 제거되지 않는다
     */
    @DisplayName("구간에 포함되지 않은 역을 제거하는 경우")
    @Test
    void 제거_실패_구간에_포함되지_않은_역을_제거하는_경우() {
        // given:
        // 삼전역, 강남역
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");
        long 석촌고분역_id = 지하철역_생성("석촌고분역");

        // 분당선 생성, 및 조회
        // 삼전역 -> 강남역
        Long lineId = 노선생성_상행종점_하행종점_등록_id응답("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);

        // when:
        // 석촌고분역 삭제
        ExtractableResponse<Response> response = 구간_삭제(lineId, 석촌고분역_id);

        // then:
        // 구간 삭제 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    long 지하철역_생성(String name) {
        final Map<String, String> param = new HashMap<>();
        param.put("name", name);
        return 지하철역_생성_성공_id_응답(param);
    }

    Map<String, Object> 노선_생성_요청파라미터(String name, String color, long upStationId, long downStationId) {
        return 노선_생성_요청파라미터(name, color, upStationId, downStationId, 10);
    }

    Map<String, Object> 노선_생성_요청파라미터(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> boondangLine = new HashMap<>();
        boondangLine.put("name", name);
        boondangLine.put("color", color);
        boondangLine.put("upStationId", upStationId);
        boondangLine.put("downStationId", downStationId);
        boondangLine.put("distance", distance);
        return boondangLine;
    }

    ExtractableResponse<Response> 노선_생성_성공(Map<String, Object> lineParams) {
        return RestAssured.given().log().all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 노선_조회(String url) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(url)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 노선_구간_생성(long lineId, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    long 노선생성_상행종점_하행종점_등록_id응답(String name, String color, long upStationId, long downStationId) {

        return 노선생성_상행종점_하행종점_등록_id응답(name, color, upStationId, downStationId, 10);
    }

    long 노선생성_상행종점_하행종점_등록_id응답(String name, String color, long upStationId, long downStationId, int distance) {

        // 노선 생성
        Map<String, Object> line = 노선_생성_요청파라미터(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(line);
        String lineUrl = createResponse.headers().get(LOCATION).getValue();

        // 노선 조회 및 id 값 반환
        ExtractableResponse<Response> response = 노선_조회(lineUrl);
        return response.body().jsonPath().getLong("id");
    }

    Map<String, Object> 구간_생성(long upStationId, long downStationId) {
        return 구간_생성(upStationId, downStationId, 10);
    }

    Map<String, Object> 구간_생성(long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    ExtractableResponse<Response> 구간_삭제(long lineId, long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    Integer getLineDistance(ExtractableResponse<Response> response) {
        ArrayList<Integer> distance = response.body().jsonPath().get("sectionResponseList.distance");
        return distance.stream().mapToInt(num -> num).sum();
    }

    Map<String, Integer> getResultSection(ExtractableResponse<Response> response, int resultIndex) {
        return (Map<String, Integer>) response.body().jsonPath().getList("sectionResponseList").get(resultIndex);
    }

    int getSectionSize(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getList("sectionResponseList").size();
    }
}
