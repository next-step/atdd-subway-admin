package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AbstractAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.AcceptanceUtils.assertStatusCode;
import static nextstep.subway.line.LineAcceptanceTest.requestApiByCreateLine;
import static nextstep.subway.line.LineAcceptanceTest.requestApiByGetLine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 선릉역;
    private StationResponse 삼성역;

    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역 = StationAcceptanceTest.createStation("교대역").extract().as(StationResponse.class);
        선릉역 = StationAcceptanceTest.createStation("선릉역").extract().as(StationResponse.class);
        강남역 = StationAcceptanceTest.createStation("강남역").extract().as(StationResponse.class);
        삼성역 = StationAcceptanceTest.createStation("삼성역").extract().as(StationResponse.class);

        신분당선 = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 교대역.getId(), 선릉역.getId(), 10)).extract().as(LineResponse.class);
    }

    /**
     * given 2개의 역과 노선을 생성하고
     * when 노선 중간에 새로운 역을 등록하면
     * then 노선 조회 시 추가된 역을 찾을 수 있다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void add_middle_section() {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(교대역.getId(), 강남역.getId(), 5));

        assertStatusCode(addSectionResponse, HttpStatus.OK);

        ValidatableResponse getLineResponse = requestApiByGetLine(신분당선.getId());
        assertThat(extractStationNames(getLineResponse)).contains("교대역", "강남역", "선릉역");
    }

    /**
     * given 2개의 역과 노선을 생성하고
     * when 노선 상행 종점에 새로운 역을 등록하면
     * then 노선 조회 시 추가된 역을 찾을 수 있다.
     */
    @DisplayName("노선 상행에 구간을 등록한다.")
    @Test
    void add_up_section() {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(강남역.getId(), 교대역.getId(), 3));

        assertStatusCode(addSectionResponse, HttpStatus.OK);

        ValidatableResponse getLineResponse = requestApiByGetLine(신분당선.getId());
        assertThat(extractStationNames(getLineResponse)).contains("교대역", "강남역", "선릉역");
    }

    /**
     * given 2개의 역과 노선을 생성하고
     * when 노선 하행 종점에 새로운 역을 등록하면
     * then 노선 조회 시 추가된 역을 찾을 수 있다.
     */
    @DisplayName("노선 하행에 구간을 등록한다.")
    @Test
    void add_down_section() {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 3));

        assertStatusCode(addSectionResponse, HttpStatus.OK);

        ValidatableResponse getLineResponse = requestApiByGetLine(신분당선.getId());
        assertThat(extractStationNames(getLineResponse)).contains("교대역", "강남역", "선릉역");
    }

    /**
     * Given 지하철 역과 노선이 등록되어있고
     * When 지하철 노선에 기존 역 사이 길이보다 크거나 같은 구간을 등록하면
     * Then 등록을 할 수 없다
     */
    @DisplayName("기존 역 사이 길이보다 크거나 같을 경우")
    @ParameterizedTest
    @ValueSource(strings = {"20", "10"})
    void add_section_distance_over(int distance) {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(교대역.getId(), 강남역.getId(), distance));

        assertStatusCode(addSectionResponse, HttpStatus.BAD_REQUEST);
    }

    public static ValidatableResponse requestApiByAddSection(Long lineId, SectionRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", lineId)
            .then().log().all();
    }

    /**
     * Given 지하철 역과 노선이 등록되어있고
     * When 지하철 노선에 이미 등록되어있는 지하철 구간을 등록하면
     * Then 등록을 할 수 없다
     */
    @DisplayName("상행역 하행역 이미 모두 등록되어있는 경우")
    @Test
    void add_section_already_exists() {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(교대역.getId(), 선릉역.getId(), 5));

        assertStatusCode(addSectionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 지하철 역과 노선이 등록되어있고
     * When 지하철 노선에 포함되어 있지 않은 지하철 역을 구간으로 등록하면
     * Then 등록을 할 수 없다
     */
    @DisplayName("상행역 하행역 둘 중 하나도 포함되어있지 않은 경우")
    @Test
    void add_section_not_contains() {
        ValidatableResponse addSectionResponse = requestApiByAddSection(신분당선.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 5));

        assertStatusCode(addSectionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * given 노선에 2개의 구간을 추가하고
     * when 중간 역을 제거하면
     * then 노선 조회 시 제거된 역을 찾을 수 없다.
     */
    @DisplayName("노선에 중간역을 삭제한다.")
    @Test
    void delete_middle_station() {
        requestApiByAddSection(신분당선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 5));

        ValidatableResponse removeResponse = requestApiByRemoveLineStation(신분당선.getId(), 선릉역.getId());
        assertStatusCode(removeResponse, HttpStatus.OK);
        ValidatableResponse response = requestApiByGetLine(신분당선.getId());

        assertAll(
            () -> assertThat(extractStationNames(response)).contains("강남역", "교대역"),
            () -> assertThat(extractStationNames(response)).doesNotContain("선릉역")
        );
    }

    /**
     * given 노선에 2개의 구간을 추가하고
     * when 상행 역을 제거하면
     * then 노선 조회 시 제거된 역을 찾을 수 없다.
     */
    @DisplayName("노선에 상행역을 삭제한다.")
    @Test
    void delete_up_station() {
        requestApiByAddSection(신분당선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 5));

        ValidatableResponse removeResponse = requestApiByRemoveLineStation(신분당선.getId(), 교대역.getId());
        assertStatusCode(removeResponse, HttpStatus.OK);
        ValidatableResponse response = requestApiByGetLine(신분당선.getId());

        assertAll(
            () -> assertThat(extractStationNames(response)).contains("강남역", "선릉역"),
            () -> assertThat(extractStationNames(response)).doesNotContain("교대역")
        );
    }

    /**
     * given 노선에 2개의 구간을 추가하고
     * when 하행 역을 제거하면
     * then 노선 조회 시 제거된 역을 찾을 수 없다.
     */
    @DisplayName("노선에 하행역을 삭제한다.")
    @Test
    void delete_down_station() {
        requestApiByAddSection(신분당선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 5));

        ValidatableResponse removeResponse = requestApiByRemoveLineStation(신분당선.getId(), 강남역.getId());
        assertStatusCode(removeResponse, HttpStatus.OK);
        ValidatableResponse response = requestApiByGetLine(신분당선.getId());

        assertAll(
            () -> assertThat(extractStationNames(response)).contains("교대역", "선릉역"),
            () -> assertThat(extractStationNames(response)).doesNotContain("강남역")
        );
    }

    /**
     * given 노선에 구간이 1개이고
     * when 하행 역을 제거하면
     * then 제거 실패한다.
     */
    @DisplayName("구간이 하나인 경우 삭제하면 실패한다.")
    @Test
    void fail_delete() {
        ValidatableResponse removeResponse = requestApiByRemoveLineStation(신분당선.getId(), 선릉역.getId());

        assertStatusCode(removeResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * given 노선에 2개의 구간을 추가하고
     * when 노선에 없는 역을 제거하면
     * then 제거 실패한다.
     */
    @DisplayName("노선에 없는 역을 삭제하는 경우 실패한다.")
    @Test
    void fail_delete_not_contains_station() {
        requestApiByAddSection(신분당선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 5));

        ValidatableResponse removeResponse = requestApiByRemoveLineStation(신분당선.getId(), 삼성역.getId());

        assertStatusCode(removeResponse, HttpStatus.BAD_REQUEST);
    }

    public static List<String> extractStationNames(ValidatableResponse response) {
        return response.extract().jsonPath().getList("stations.name", String.class);
    }

    private static ValidatableResponse requestApiByRemoveLineStation(long lineId, long stationId) {
        return RestAssured.given().log().all()
            .param("stationId", stationId)
            .when().delete("/lines/{lineId}/sections", lineId)
            .then().log().all();
    }
}
