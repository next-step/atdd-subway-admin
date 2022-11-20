package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationResponse;
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

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    private static String SELECT_LINE_LOCATION = null;
    private static String SELECT_SECTION_LOCATION = null;
    private static String INSERT_SECTION_LOCATION = null;

    private static int LINE_STATION_DISTANCE = 0;

    private static int LINE_STATION_SIZE = 4;
    private static int LESS_THAN_DISTANCE = 3;
    private static int GATHER_THAN_DISTANCE = 15;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private InitializationEntity initializationEntity;

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
        initializationEntity.initLine();
        initializationEntity.initSection();

        SELECT_LINE_LOCATION = "/lines/" + line_1.getId();
        SELECT_SECTION_LOCATION = "/sections/" + section_1.getId();
        INSERT_SECTION_LOCATION = SELECT_LINE_LOCATION + "/sections";

        LINE_STATION_DISTANCE = section_1.getDistance();
    }

    /**
     * When 상행역과 하행역 사이의 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 각 구간의 길이가 변경되며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionUpStationInside() {
        // when
        executeRestEntity.insertSectionSuccess(
                INSERT_SECTION_LOCATION,
                executeRestEntity.generateSectionRequest(upStation.getId(), newStation.getId(), LESS_THAN_DISTANCE));

        // then
        checkCreateSuccessCondition(SELECT_LINE_LOCATION, LINE_STATION_SIZE);
        checkResetSectionDistance(SELECT_SECTION_LOCATION, LINE_STATION_DISTANCE - LESS_THAN_DISTANCE);
    }

    /**
     * When 상행종점역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 기존 구간의 길이가 변경되지 않으며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createSectionUpStation() {
        // when
        executeRestEntity.insertSectionSuccess(
                INSERT_SECTION_LOCATION,
                executeRestEntity.generateSectionRequest(newStation.getId(), upStation.getId(), LESS_THAN_DISTANCE));

        // then
        checkCreateSuccessCondition(SELECT_LINE_LOCATION, LINE_STATION_SIZE);
        checkResetSectionDistance(SELECT_SECTION_LOCATION, LINE_STATION_DISTANCE
        );
    }

    /**
     * When 하행종점역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 기존 구간의 길이가 변경되지 않으며
     * Then 지하철 노선 조회시 기존 노선보다 1개가 추가된 갯수의 지하철 역을 가지게 된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createSectionDownStation() {
        // when
        executeRestEntity.insertSectionSuccess(
                INSERT_SECTION_LOCATION,
                executeRestEntity.generateSectionRequest(downStation.getId(), newStation.getId(), LESS_THAN_DISTANCE));

        // then
        checkCreateSuccessCondition(SELECT_LINE_LOCATION, LINE_STATION_SIZE);
        checkResetSectionDistance(SELECT_SECTION_LOCATION, LINE_STATION_DISTANCE);
    }

    /**
     * When 상행역과 하행역 사이의 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void createFailSectionSameAndGatherDistance() {
        // when
        ExtractableResponse<Response> response = executeRestEntity
                .insert(executeRestEntity.generateSectionRequest(
                        upStation.getId(), newStation.getId(), GATHER_THAN_DISTANCE), INSERT_SECTION_LOCATION).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다.")
    @Test
    void createFailSectionSameStation() {
        // when
        ExtractableResponse<Response> response = executeRestEntity
                .insert(executeRestEntity.generateSectionRequest(
                        upStation.getId(), mediumStation.getId(), LESS_THAN_DISTANCE), INSERT_SECTION_LOCATION).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록할 수 없다.")
    @Test
    void createFailSectionNoneStation() {
        // when
        ExtractableResponse<Response> response = executeRestEntity
                .insert(executeRestEntity.generateSectionRequest(
                        upStation.getId(), 999L, LESS_THAN_DISTANCE), INSERT_SECTION_LOCATION).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void checkCreateSuccessCondition(String location, int stationSize) {
        List<StationResponse> stations = executeRestEntity
                .selectLine(location)
                .extract()
                .jsonPath()
                .getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(stationSize);
    }

    private void checkResetSectionDistance(String location, int distance) {
        int resetDistance = executeRestEntity.selectSection(location).extract().jsonPath().get("distance");
        assertThat(resetDistance).isEqualTo(distance);
    }

}
