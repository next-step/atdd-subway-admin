package nextstep.subway.section;

import io.restassured.RestAssured;
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

import java.util.List;

import static nextstep.subway.util.InitializationEntity.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

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
        initializationEntity.initLineStation();
        initializationEntity.initLine();
    }

    /**
     * When 상행역을 기준으로 상행역과 하행역 사이의 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 지하철 노선 조회시 3개의 지하철 역을 가지게 된다.
     */
    @DisplayName("상행역을 기준으로 역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionUpStationInside() {
        String location = "/lines/" + line_1.getId();
        // when
        executeRestEntity.insertSectionSuccess(
                location, executeRestEntity.generateSectionRequest(upStation.getId(), station_3.getId(), 5));

        // then
        List<StationResponse> stations = executeRestEntity
                .selectLine(location)
                .extract()
                .jsonPath()
                .getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(3);
    }

    /**
     * When 하행역을 기준으로 상행역과 하행역 사이의 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 지하철 노선 조회시 3개의 지하철 역을 가지게 된다.
     */
    @DisplayName("하행역을 기준으로 역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionDownStationInside() {
        String location = "/lines/" + line_1.getId();
        // when
        executeRestEntity.insertSectionSuccess(
                location, executeRestEntity.generateSectionRequest(station_3.getId(), downStation.getId(), 5));

        // then
        List<StationResponse> stations = executeRestEntity
                .selectLine(location)
                .extract()
                .jsonPath()
                .getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(3);
    }

    /**
     * When 상행역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 지하철 노선 조회시 3개의 지하철 역을 보유하고 있고
     * Then 상행역이 새로운 역으로 변경되어 있다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createSectionUpStation() {

    }

    /**
     * When 하행역으로 새로운 역을 등록하면
     * Then 정상적으로 등록되고
     * Then 지하철 노선 조회시 3개의 지하철 역을 보유하고 있고
     * Then 하행역이 새로운 역으로 변경되어 있다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createSectionDownStation() {

    }

    /**
     * When 상행역과 하행역 사이의 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void createFailSectionSameAndGatherDistance() {

    }

    /**
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다.")
    @Test
    void createFailSectionSameStation() {

    }

    /**
     * When 상행역과 하행역과 동일한 구조의 새로운 역을 등록하면
     * Then 정상적으로 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록할 수 없다.")
    @Test
    void createFailSectionNoneStation() {

    }

}
