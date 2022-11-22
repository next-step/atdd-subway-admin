package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.util.DatabaseCleanup;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철호선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private InitializationEntity initializationEntity;

    @Autowired
    private LineExecuteRestEntity lineExecuteRestEntity;

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
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String location = createLineSuccess("1호선", upStation, downStation).header("Location");

        // then
        String lineName = lineExecuteRestEntity.selectLine(location).extract().jsonPath().get("name");
        assertThat(lineName).isEqualTo("1호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLineSuccess("1호선", upStation, downStation);
        createLineSuccess("2호선", mediumStation, newStation);

        // when
        List<String> lineNames =
                lineExecuteRestEntity.selectLines().extract().jsonPath().getList("name", String.class);

        // then
        assertAll (
                () -> assertThat(lineNames.size()).isEqualTo(2),
                () -> assertThat(lineNames).contains("1호선", "2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String location = createLineSuccess("1호선", upStation, downStation).header("Location");

        // when
        String lineName = lineExecuteRestEntity.selectLine(location).extract().jsonPath().get("name");

        // then
        assertThat(lineName).isNotNull();
        assertThat(lineName).isEqualTo("1호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String location = createLineSuccess("1호선", upStation, downStation).header("Location");

        // when
        lineExecuteRestEntity.updateLineSuccess(location,
                lineExecuteRestEntity.generateLineRequest("2호선", mediumStation.getId(), newStation.getId()));

        // then
        String lineName = lineExecuteRestEntity.selectLine(location).extract().jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        String location = createLineSuccess("1호선", upStation, downStation).header("Location");

        // when
        lineExecuteRestEntity.deleteLineSuccess(location);

        // then
        String lineName = lineExecuteRestEntity.selectLine(location).extract().jsonPath().get("name");
        assertThat(lineName).isNullOrEmpty();
    }

    private ExtractableResponse<Response> createLineSuccess(String name, Station upStation, Station downStation) {
        return lineExecuteRestEntity.insertLineSuccess(
                lineExecuteRestEntity.generateLineRequest(name, upStation.getId(), downStation.getId()));
    }

}
