package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.util.LineAcceptanceMethods;
import nextstep.subway.util.StationAcceptanceMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 구간 관련 기능")
class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long defaultLineId;
    private Long defaultUpStationId;
    private Long defaultDownStationId;

    @BeforeEach
    @Order(1)
    public void setUp() {
        defaultUpStationId = StationAcceptanceMethods.createStation("강남역").jsonPath().getLong("id");
        defaultDownStationId = StationAcceptanceMethods.createStation("광교중앙역").jsonPath().getLong("id");
        defaultLineId = LineAcceptanceMethods.createLine("신분당선", "red", defaultUpStationId, defaultDownStationId, 10).jsonPath()
                .getLong("id");
    }

    /**
     * Given 지하철 노선을 생성하고 When 구간을 추가하면 Then 지하철 노선 조회 시 추가된 역이 조회된다.
     */
    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long midStationId = StationAcceptanceMethods.createStation("동천역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, midStationId, defaultDownStationId, 3);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf("동천역");
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 상행 종점 구간을 추가하면 Then 노선 조회시 첫번째 역으로 변경된 상행 종점이 조회된다
     */
    @DisplayName("지하철 상행 종점 추가")
    @Test
    void addFirstSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long upStationId = StationAcceptanceMethods.createStation("신논현").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, upStationId, defaultUpStationId, 5);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("신논현", "강남역", "광교중앙역");
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 하행 종점 구간을 추가하면 Then 노선 조회시 마지막 역으로 수정된 하행 종점이 조회된다
     */
    @DisplayName("지하철 하행 종점 추가")
    @Test
    void addLastSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "광교중앙역", "광교역");
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 중간 구간을 추가하면 Then 노선 조회시 역 목록 중간에 신규 추가한 중간 구간의 역이 조회된다
     */
    @DisplayName("지하철 중간 구간 추가")
    @Test
    void addMiddleSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        Long middleStationId = StationAcceptanceMethods.createStation("판교역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, middleStationId, defaultDownStationId, 3);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "판교역", "광교중앙역", "광교역");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성시 구간 길이보다 더 긴 중간 구간을 추가할 경우 Then 구간이 생성되지 않는다
     */
    @DisplayName("현재 구간 보다 거리가 긴 중간 구간 추가시 생성 안됨")
    @Test
    void addLongSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long middleStationId = StationAcceptanceMethods.createStation("동천역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = LineAcceptanceMethods.addSection(defaultLineId, middleStationId,
                defaultDownStationId, 15);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고 When 이미 존재하는 구간을 추가할 경우 Then 구간이 생성되지 않는다
     */
    @DisplayName("이미 존재하는 구간 추가시 생성 안됨")
    @Test
    void addExistsSection() {
        // when
        // BeforeEach 에서 실행

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.addSection(defaultLineId, defaultUpStationId,
                defaultDownStationId, 3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고 상행 구간을 추가하고 When 구간 추가시 상행역과 하행역이 이미 구간에 포함되어 있는 경우 Then 구간이 생성되지 않는다
     */
    @DisplayName("신규 구간의 상행역과 하행역이 모두 구간에 포함되어 있을 경우 생성 안됨")
    @Test
    void addSectionOfExistStations() {
        // when
        // BeforeEach 에서 실행
        Long upStationId = StationAcceptanceMethods.createStation("신논현").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, upStationId, defaultUpStationId, 5);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.addSection(defaultLineId, defaultDownStationId,
                defaultUpStationId, 4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고 중간역을 추가 한 뒤
     * When 상행 종점을 제거하면
     * Then 지하철 노선 조회 시 중간역이 첫번째 역으로 조회된다.
     */
    @DisplayName("노선 상행종점 제거")
    @Test
    void removeSectionUpStation() {
        // when
        // BeforeEach 에서 실행
        Long midStationId = StationAcceptanceMethods.createStation("동천역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, midStationId, defaultDownStationId, 3);

        // then
        LineAcceptanceMethods.removeSection(defaultLineId, defaultUpStationId);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("동천역", "광교중앙역");
    }

    /**
     * Given 지하철 노선을 생성하고 중간역을 추가 한 뒤
     * When 하행 종점을 제거하면
     * Then 지하철 노선 조회 시 중간역이 마지막 역으로 조회된다.
     */
    @DisplayName("노선 하행종점 제거")
    @Test
    void removeSectionDownStation() {
        // when
        // BeforeEach 에서 실행
        Long midStationId = StationAcceptanceMethods.createStation("동천역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, midStationId, defaultDownStationId, 3);

        // then
        LineAcceptanceMethods.removeSection(defaultLineId, defaultDownStationId);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "동천역");
    }

    /**
     * Given 지하철 노선을 생성하고 하행 종점을 추가 한 뒤
     * When 중간역을 제거하면
     * Then 지하철 노선 조회 시 중간역이 조회되지 않는다.
     */
    @DisplayName("노선 중간역 제거")
    @Test
    void removeSectionMidStation() {
        // when
        // BeforeEach 에서 실행
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        // then
        LineAcceptanceMethods.removeSection(defaultLineId, defaultDownStationId);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "광교역");
    }

    /**
     * Given 지하철 노선을 생성하고 노선상에 상행종점과 하행종점 만이 존재하는 상태에서
     * When 역을 하나 제거할 경우
     * Then 지하철 노선 조회 시 역이 제거되지 않는 것을 확인.
     */
    @DisplayName("구간 1개 일 때 역 제거")
    @Test
    void removeSectionWhenOnlyOneSection() {
        // when
        // BeforeEach 에서 실행

        // then
        LineAcceptanceMethods.removeSection(defaultLineId, defaultDownStationId);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "광교중앙역");
    }

    /**
     * Given 지하철 노선을 생성하고 2개의 구간이 존재하는 상태에서
     * When 구간상에 존재하지 않는 역을 제거할 경우
     * Then 지하철 노선 조회 시 아무 역도 제거되지 않는 것을 확인
     */
    @DisplayName("구간 상에 존재하지 않는 역 제거")
    @Test
    void removeSectionWithNotExistsStation() {
        // when
        // BeforeEach 에서 실행
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        LineAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        // then
        Long stationId = StationAcceptanceMethods.createStation("사당역").jsonPath().getLong("id");
        LineAcceptanceMethods.removeSection(defaultLineId, stationId);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "광교중앙역", "광교역");
    }
}
