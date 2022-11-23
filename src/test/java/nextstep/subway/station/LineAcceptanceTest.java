package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DataBaseCleaner;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.fixture.StationTestFixture;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.rest.LineRestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private StationRepository stationRepository;

    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    public void setUp() {
        if(RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dataBaseCleaner.clear();
        강남역 = stationRepository.save(StationTestFixture.강남역);
        역삼역 = stationRepository.save(StationTestFixture.역삼역);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createStationLine_test() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 3);

        // when
        LineRestAssured.지하철_노선_생성(request);

        // then
        ExtractableResponse<Response> response = LineRestAssured.지하철_노선_목록_조회();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name", String.class)).containsAnyOf("신분당선")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * TThen 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines_test() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        LineRestAssured.지하철_노선_생성(request);

        request = new LineCreateRequest("2호선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        LineRestAssured.지하철_노선_생성(request);

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철_노선_목록_조회();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name", String.class)).hasSize(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getOneLine_test() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        ExtractableResponse<Response> createResponse = LineRestAssured.지하철_노선_생성(request);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> findLineResponse = LineRestAssured.지하철_노선_조회(lineId);

        // then
        assertAll(
                () -> assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findLineResponse.jsonPath().getLong("id")).isEqualTo(lineId)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateStationLine_test() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        ExtractableResponse<Response> createResponse = LineRestAssured.지하철_노선_생성(request);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(lineId, "2호선", "bg-blue-600");
        ExtractableResponse<Response> response = LineRestAssured.지하철_노선_수정(lineUpdateRequest, lineId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("bg-blue-600")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteStationLine_test() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        ExtractableResponse<Response> createResponse = LineRestAssured.지하철_노선_생성(request);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철_노선_삭제(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
