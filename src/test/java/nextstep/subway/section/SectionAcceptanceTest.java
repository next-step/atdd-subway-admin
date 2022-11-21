package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineExecuteRestEntity;
import nextstep.subway.util.DatabaseCleanup;
import nextstep.subway.util.ExecuteRestEntity;
import nextstep.subway.util.InitializationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.util.InitializationEntity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    private static int LINE_STATION_SIZE = 4;
    private static int LESS_THAN_DISTANCE = 3;
    private static int GATHER_THAN_DISTANCE = 15;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private InitializationEntity initializationEntity;

    @Autowired
    private SectionExecuteRestEntity sectionExecuteRestEntity;

    @Autowired
    private LineExecuteRestEntity lineExecuteRestEntity;

    @Autowired
    private ExecuteRestEntity executeRestEntity;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        initializationEntity.initStations();
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 상행역과 하행역 사이의 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 각 구간의 길이가 변경되며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionUpStationInside() {
        // given
        Line line = createLine("1호선");
        Section preSection = addedSections(line, upStation, mediumStation, 5);
        Section postSection = addedSections(line, mediumStation, downStation, 7);

        // when
        createSectionSuccess(line.getId(), upStation, newStation, LESS_THAN_DISTANCE);

        // then
        checkSectionStationSize(line.getId(), LINE_STATION_SIZE);
        checkSectionDistance(preSection.getId(), preSection.getDistance() - LESS_THAN_DISTANCE);
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 상행종점역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 기존 구간의 길이가 변경되지 않으며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createSectionUpStation() {
        // given
        Line line = createLine("1호선");
        Section preSection = addedSections(line, upStation, mediumStation, 5);
        Section postSection = addedSections(line, mediumStation, downStation, 7);

        // when
        createSectionSuccess(line.getId(), newStation, upStation, LESS_THAN_DISTANCE);

        // then
        checkSectionStationSize(line.getId(), LINE_STATION_SIZE);
        checkSectionDistance(preSection.getId(), preSection.getDistance());
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 하행종점역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 기존 구간의 길이가 변경되지 않으며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createSectionDownStation() {
        // given
        Line line = createLine("1호선");
        Section preSection = addedSections(line, upStation, mediumStation, 5);
        Section postSection = addedSections(line, mediumStation, downStation, 7);

        // when
        createSectionSuccess(line.getId(), downStation, newStation, LESS_THAN_DISTANCE);

        // then
        checkSectionStationSize(line.getId(), LINE_STATION_SIZE);
        checkSectionDistance(preSection.getId(), preSection.getDistance());
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 상행역과 하행역 사이의 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void createFailSectionSameAndGatherDistance() {
        // given
        Line line = createLine("1호선");
        addedSections(line, upStation, mediumStation, 5);
        addedSections(line, mediumStation, downStation, 7);

        // when
        ExtractableResponse<Response> response = createSection(line.getId(), upStation, newStation, GATHER_THAN_DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다.")
    @Test
    void createFailSectionSameStation() {
        // given
        Line line = createLine("1호선");
        addedSections(line, upStation, mediumStation, 5);
        addedSections(line, mediumStation, downStation, 7);

        // when
        ExtractableResponse<Response> responseCase1 = createSection(line.getId(), upStation, mediumStation, LESS_THAN_DISTANCE);
        ExtractableResponse<Response> responseCase2 = createSection(line.getId(), mediumStation, upStation, LESS_THAN_DISTANCE);
        ExtractableResponse<Response> responseCase3 = createSection(line.getId(), mediumStation, downStation, LESS_THAN_DISTANCE);

        // then
        assertAll (
                () -> assertThat(responseCase1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(responseCase2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(responseCase3.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * Given 새로운 노선을 생성하고
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록할 수 없다.")
    @Test
    void createFailSectionNoneStation() {
        // given
        Line line = createLine("1호선");
        addedSections(line, upStation, mediumStation, 5);
        addedSections(line, mediumStation, downStation, 7);

        // when
        ExtractableResponse<Response> response = createSection(line.getId(), upStation, null, LESS_THAN_DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Line createLine(String name) {
        return initializationEntity.createLine(name);
    }

    private void createSectionSuccess(Long id, Station upStation, Station downStation, int distance) {
        sectionExecuteRestEntity.insertSectionSuccess(
                "/lines/" + id + "/sections",
                sectionExecuteRestEntity.generateSectionRequest(
                        upStation.getId(), downStation.getId(), distance));
    }

    private Section addedSections(Line line, Station upStation, Station downStation, int distance) {
        return initializationEntity.addedSections(line, upStation, downStation, distance);
    }

    private void checkSectionStationSize(Long id, int stationSize) {
        List<StationResponse> stations = lineExecuteRestEntity
                .selectLine("/lines/" + id)
                .extract()
                .jsonPath()
                .getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(stationSize);
    }

    private void checkSectionDistance(Long id, int distance) {
        int resetDistance = sectionExecuteRestEntity
                .selectSection("/sections/" + id)
                .extract()
                .jsonPath()
                .get("distance");
        assertThat(resetDistance).isEqualTo(distance);
    }

    private ExtractableResponse<Response> createSection(Long id, Station upStation, Station downStation, int distance) {
        Long downStationId = 999L;
        if (downStation != null) {
            downStationId = downStation.getId();
        }
        return executeRestEntity
                .insert(sectionExecuteRestEntity.generateSectionRequest(
                        upStation.getId(), downStationId, distance),
                        "/lines/" + id + "/sections").extract();
    }

}
