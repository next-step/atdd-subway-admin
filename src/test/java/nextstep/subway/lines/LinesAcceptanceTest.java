package nextstep.subway.lines;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.helper.DatabaseCleanup;
import nextstep.subway.helper.LineRequest;
import nextstep.subway.helper.StationRequest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 api 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class LinesAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLinesTest() {
        // given
        String lineName = "신분당선";
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        ExtractableResponse<Response> response = LineRequest
                .createLine(lineName, "bg-red-600", upStationId, downStationId, 10L);

        // when
        List<String> lineNames = LineRequest.getAllLines()
                .jsonPath()
                .getList("name", String.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(lineNames).containsAnyOf(lineName);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getAllLinesTest() {
        // given
        String lineName1 = "신분당선";
        String lineName2 = "분당선";
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        LineRequest.createLine(lineName1, "bg-red-600", upStationId, downStationId, 10L);
        LineRequest.createLine(lineName2, "bg-green-600", upStationId, downStationId, 10L);

        // when
        List<String> lineNames = LineRequest.getAllLines()
                .jsonPath()
                .getList("name", String.class);

        // then
        assertThat(lineNames).containsAnyOf(lineName1, lineName2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLinesTest() {
        // given
        String lineName = "신분당선";
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                lineName, "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> response = LineRequest.getLineById(createdLineId);
        List<String> stationNames = response.jsonPath()
                .getList("stations.name", String.class);
        String getResultLineName = response.jsonPath()
                .get("name");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(stationNames).containsAll(Arrays.asList(stationNames.get(0), stationNames.get(0)));
        assertThat(getResultLineName).isEqualTo(lineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLinesTest() {
        // given
        String lineName = "신분당선";
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                lineName, "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        String updateName = "다른분당선";
        ExtractableResponse<Response> response = LineRequest
                .updateLine(createdLineId, updateName, "bg-red-600");
        String changedName = LineRequest.getLineById(createdLineId)
                .jsonPath()
                .get("name");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(changedName).isNotEqualTo(lineName);
        assertThat(changedName).isEqualTo(updateName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLinesTest() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> deleteResponse = LineRequest.deleteLine(createdLineId);
        List<Long> lineIds = LineRequest.getAllLines()
                .jsonPath()
                .getList("id", Long.class);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(lineIds).doesNotContain(createdLineId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선 중간에 지하철을 추가하면
     * Then 해당 지하철 노선 정보가 추가된다
     */
    @DisplayName("기존 지하철 노선에 중간 노선을 추가한다")
    @Test
    void addMiddleSection() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long middleStationId = StationRequest.createStationThenReturnId("다른지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> addLineResponse = LineRequest
                .addSection(createdLineId, upStationId, middleStationId, 4L);

        // then
        assertThat(addLineResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(addLineResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(upStationId, middleStationId, downStationId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행 종점, 상행 종점을 추가하면
     * Then 해당 지하철 노선 정보가 추가된다
     */
    @DisplayName("기존 지하철 노선에 하행 종점, 상행 종점을 추가한다")
    @Test
    void addFirstOrLastSection() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long otherStationId = StationRequest.createStationThenReturnId("다른지하철역");
        Long anotherStationId = StationRequest.createStationThenReturnId("또다른지하철역");
        Long createdLineId1 = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Long createdLineId2 = LineRequest.createLineThenReturnId(
                "분당선", "bg-green-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> newFirstResponse = LineRequest
                .addSection(createdLineId1, otherStationId, upStationId, 4L);
        ExtractableResponse<Response> newLastResponse = LineRequest
                .addSection(createdLineId2, downStationId, anotherStationId, 4L);

        // then
        assertThat(newFirstResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(newLastResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(newFirstResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(otherStationId, upStationId, downStationId);
        assertThat(newLastResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(upStationId, downStationId, anotherStationId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선 중간에 원본 노선의 길이보다 크거나 같은 노선을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("기존 지하철 노선의 길이보다 크거나 같은 중간 노선을 추가하면 실패된다")
    @Test
    void addMiddleSectionByLongerOrSameDistance() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long otherStationId = StationRequest.createStationThenReturnId("다른지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> sameDistanceResponse = LineRequest
                .addSection(createdLineId, upStationId, otherStationId, 10L);
        ExtractableResponse<Response> longerDistanceResponse = LineRequest
                .addSection(createdLineId, upStationId, otherStationId, 11L);

        // then
        assertThat(sameDistanceResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(longerDistanceResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 노선의 상행, 하행 노선이 같은 노선을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("추가하려는 노선의 상행, 하행 노선이 추가되는 노선의 상행, 하행이 같은 노선을 추가하면 실패한다")
    @Test
    void addSameUpAndDownStationLine() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> sameStationLineResponse = LineRequest
                .addSection(createdLineId, upStationId, downStationId, 7L);

        // then
        assertThat(sameStationLineResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 노선에 존재하지 않는 상/하행을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("추가하려는 노선의 상행, 하행 노선이 추가되는 노선의 상행, 하행의 모든 노선과 다른 노선을 추가하면 실패한다")
    @Test
    void addNothingMatchUpAndDownStationLine() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("지하철역");
        Long downStationId = StationRequest.createStationThenReturnId("새로운지하철역");
        Long otherStationId = StationRequest.createStationThenReturnId("다른지하철역");
        Long anotherStationId = StationRequest.createStationThenReturnId("또다른지하철역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> sameStationLineResponse = LineRequest
                .addSection(createdLineId, otherStationId, anotherStationId, 7L);

        // then
        assertThat(sameStationLineResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }
}
