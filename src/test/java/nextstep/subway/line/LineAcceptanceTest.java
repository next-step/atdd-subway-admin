package nextstep.subway.line;

import static nextstep.subway.line.LineTestFixtures.노선_목록조회;
import static nextstep.subway.line.LineTestFixtures.노선_삭제;
import static nextstep.subway.line.LineTestFixtures.노선_생성;
import static nextstep.subway.line.LineTestFixtures.노선_생성_값_리턴;
import static nextstep.subway.line.LineTestFixtures.노선_수정;
import static nextstep.subway.line.LineTestFixtures.노선_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.fixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends TestFixtures {

    @Autowired
    StationRepository stationRepository;
    String stationId1 = "";
    String stationId2 = "";
    String stationId3 = "";

    @BeforeEach
    void beforeEach() {
        setUp();
        Station station1 = stationRepository.save(new Station("경기 광주역"));
        Station station2 = stationRepository.save(new Station("중앙역"));
        Station station3 = stationRepository.save(new Station("모란역"));
        stationId1 = String.valueOf(station1.getId());
        stationId2 = String.valueOf(station2.getId());
        stationId3 = String.valueOf(station3.getId());
    }

    /**
     * When 지하철 노선을 생성하면
     * <p>
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        노선_생성("신분당선", "bg-red-600", stationId1, stationId2, "10");
        //then
        List<String> lineNames = 노선_목록조회("name");

        //then
        assertThat(lineNames).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * <p>
     * When 지하철 노선 목록을 조회하면
     * <p>
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        노선_생성("신분당선", "bg-red-600", stationId1, stationId2, "10");
        노선_생성("분당선", "bg-green-600", stationId1, stationId3, "10");

        //when
        List<String> lineNames = 노선_목록조회("name");

        //then
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 조회하면
     * <p>
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineByName() {
        //given
        노선_생성("신분당선", "bg-red-600", stationId1, stationId2, "10");

        //when
        String lineName = 노선_조회("/{name}", "신분당선", "name");

        //then
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 수정하면
     * <p>
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void modifyLine() {
        //given
        노선_생성("신분당선", "bg-red-600", stationId1, stationId2, "10");

        //when
        ExtractableResponse<Response> response =
                노선_수정("신분당선2", "bg-green-600", stationId1, stationId2, "10", "/{name}", "신분당선");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 삭제하면
     * <p>
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 정보를 제거한다.")
    @Test
    void deleteLine() {
        //given
        String id = 노선_생성_값_리턴("신분당선", "bg-red-600", stationId1, stationId2, "10", "id");

        //when
        ExtractableResponse<Response> response = 노선_삭제("/{id}", id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
