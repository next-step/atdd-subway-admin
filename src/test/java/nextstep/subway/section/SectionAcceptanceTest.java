package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.util.LineAcceptanceMethods;
import nextstep.subway.util.SectionAcceptanceMethods;
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
        SectionAcceptanceMethods.addSection(defaultLineId, midStationId, defaultDownStationId, 3);

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getLine(defaultLineId);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf("동천역");
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 상행 종점 구간을 추가하면 Then 구간 목록 조회시 첫번째 구간으로 조회된다.
     */
    @DisplayName("지하철 상행 종점 추가")
    @Test
    void addFirstSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long upStationId = StationAcceptanceMethods.createStation("신논현").jsonPath().getLong("id");
        SectionAcceptanceMethods.addSection(defaultLineId, upStationId, defaultUpStationId, 5);

        // then
        ExtractableResponse<Response> response = SectionAcceptanceMethods.getAllSections(defaultLineId);
        assertThat(response.jsonPath().getLong("[0].upStation.id")).isEqualTo(upStationId);
        assertThat(response.jsonPath().getLong("[0].downStation.id")).isEqualTo(defaultUpStationId);
        assertThat(response.jsonPath().getInt("[0].distance")).isEqualTo(5);
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 하행 종점 구간을 추가하면 Then 구간 목록 조회시 마지막 구간으로 조회된다.
     */
    @DisplayName("지하철 하행 종점 추가")
    @Test
    void addLastSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        SectionAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        // then
        ExtractableResponse<Response> response = SectionAcceptanceMethods.getAllSections(defaultLineId);
        assertThat(response.jsonPath().getLong("[1].upStation.id")).isEqualTo(defaultDownStationId);
        assertThat(response.jsonPath().getLong("[1].downStation.id")).isEqualTo(downStationId);
        assertThat(response.jsonPath().getInt("[1].distance")).isEqualTo(7);
    }

    /**
     * Given 지하철 노선을 생성하고 When 지하철 중간 구간을 추가하면 Then 구간 목록 조회시 중간 구간으로 조회된다.
     */
    @DisplayName("지하철 중간 구간 추가")
    @Test
    void addMiddleSection() {
        // when
        // BeforeEach 에서 실행

        // then
        Long downStationId = StationAcceptanceMethods.createStation("광교역").jsonPath().getLong("id");
        SectionAcceptanceMethods.addSection(defaultLineId, defaultDownStationId, downStationId, 7);

        Long middleStationId = StationAcceptanceMethods.createStation("판교역").jsonPath().getLong("id");
        SectionAcceptanceMethods.addSection(defaultLineId, middleStationId, defaultDownStationId, 3);

        // then
        ExtractableResponse<Response> response = SectionAcceptanceMethods.getAllSections(defaultLineId);
        assertThat(response.jsonPath().getLong("[1].upStation.id")).isEqualTo(middleStationId);
        assertThat(response.jsonPath().getLong("[1].downStation.id")).isEqualTo(defaultDownStationId);
        assertThat(response.jsonPath().getInt("[1].distance")).isEqualTo(3);
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
        ExtractableResponse<Response> response = SectionAcceptanceMethods.addSection(defaultLineId, middleStationId,
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
        ExtractableResponse<Response> response = SectionAcceptanceMethods.addSection(defaultLineId, defaultUpStationId,
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
        SectionAcceptanceMethods.addSection(defaultLineId, upStationId, defaultUpStationId, 5);

        // then
        ExtractableResponse<Response> response = SectionAcceptanceMethods.addSection(defaultLineId, defaultDownStationId,
                defaultUpStationId, 4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
