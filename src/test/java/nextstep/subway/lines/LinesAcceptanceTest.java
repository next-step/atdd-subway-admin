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
     * When 상행 또는 하행역 정보없이 노선을 생성하면
     * Then 지하철 생성이 실패되어야 한다
     */
    @DisplayName("상행 또는 하행역 정보 없이 노선을 생성하면 실패해야 한다")
    @Test
    void createLineWithoutStationTest() {
        // given
        Long StationId = StationRequest.createStationThenReturnId("지하철역");

        // when
        ExtractableResponse<Response> nonExistUpStationResponse = LineRequest
                .createLine("노선1", "bg-red-600", null, StationId, 10L);
        ExtractableResponse<Response> nonExistDownStationResponse = LineRequest
                .createLine("노선2", "bg-red-600", StationId, null, 10L);
        ExtractableResponse<Response> nonExistStationResponse = LineRequest
                .createLine("노선3", "bg-red-600", null, null, 10L);

        // then
        assertThat(nonExistUpStationResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(nonExistDownStationResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(nonExistStationResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
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
        Long upStationId = StationRequest.createStationThenReturnId("신사역");
        Long downStationId = StationRequest.createStationThenReturnId("논현역");
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
        Long upStationId = StationRequest.createStationThenReturnId("미금역");
        Long downStationId = StationRequest.createStationThenReturnId("정자역");
        LineRequest.createLine(lineName1, "bg-red-600", upStationId, downStationId, 10L);
        LineRequest.createLine(lineName2, "bg-yellow-600", upStationId, downStationId, 10L);

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
        String lineName = "2호선";
        Long upStationId = StationRequest.createStationThenReturnId("잠실역");
        Long downStationId = StationRequest.createStationThenReturnId("잠실새내");
        Long createdLineId = LineRequest.createLineThenReturnId(
                lineName, "bg-green-600", upStationId, downStationId, 10L
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
        String lineName = "1호선";
        Long upStationId = StationRequest.createStationThenReturnId("시청역");
        Long downStationId = StationRequest.createStationThenReturnId("신도림역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                lineName, "bg-blue-600", upStationId, downStationId, 10L
        );

        // when
        String updateName = "2호선";
        ExtractableResponse<Response> response = LineRequest
                .updateLine(createdLineId, updateName, "bg-green-600");
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
        Long upStationId = StationRequest.createStationThenReturnId("양재시민의숲");
        Long downStationId = StationRequest.createStationThenReturnId("청계산입구");
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
        Long upStationId = StationRequest.createStationThenReturnId("광교역");
        Long downStationId = StationRequest.createStationThenReturnId("상현역");
        Long middleStationId = StationRequest.createStationThenReturnId("광교중앙역");
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
        Long upStationId = StationRequest.createStationThenReturnId("정왕역");
        Long downStationId = StationRequest.createStationThenReturnId("신길온천역");
        Long otherStationId = StationRequest.createStationThenReturnId("오이도역");
        Long anotherStationId = StationRequest.createStationThenReturnId("안산역");
        Long createdLineId1 = LineRequest.createLineThenReturnId(
                "분당선", "bg-yellow-600", upStationId, downStationId, 10L
        );
        Long createdLineId2 = LineRequest.createLineThenReturnId(
                "4호선", "bg-sky-600", upStationId, downStationId, 10L
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
        Long upStationId = StationRequest.createStationThenReturnId("성복역");
        Long downStationId = StationRequest.createStationThenReturnId("수지구청역");
        Long otherStationId = StationRequest.createStationThenReturnId("동천역");
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
        Long upStationId = StationRequest.createStationThenReturnId("인천역");
        Long downStationId = StationRequest.createStationThenReturnId("신포역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "분당선", "bg-yellow-600", upStationId, downStationId, 10L
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
    @DisplayName("추가하려는 노선의 상행, 하행 노선이 추가되는 노선에 포함되어있지 않으면 노선 추가가 실패된다")
    @Test
    void addNothingMatchUpAndDownStationLine() {
        // given
        Long upStationId = StationRequest.createStationThenReturnId("인천논현");
        Long downStationId = StationRequest.createStationThenReturnId("소래포구");
        Long otherStationId = StationRequest.createStationThenReturnId("송도역");
        Long anotherStationId = StationRequest.createStationThenReturnId("연수역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "분당선", "bg-yellow-600", upStationId, downStationId, 10L
        );

        // when
        ExtractableResponse<Response> sameStationLineResponse = LineRequest
                .addSection(createdLineId, otherStationId, anotherStationId, 7L);

        // then
        assertThat(sameStationLineResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Given 2개 이상의 구간을 가진 지하철 노선을 생성하고
     * When 구간을 삭제하면
     * Then 해당 구간이 삭제되고, 해당 구간의 앞/뒤 구간이 연결되고 길이는 합이 되어야 한다
     */
    @DisplayName("지하철 노선의 구간을 삭제하면 해당 구간이 삭제되고 해당 구간의 상/하행이 연결되고 길이는 합이 되어야 한다")
    @Test
    void deleteSection() {
        // given
        Long station1 = StationRequest.createStationThenReturnId("소요산역");
        Long station2 = StationRequest.createStationThenReturnId("동두천역");
        Long station3 = StationRequest.createStationThenReturnId("보산역");
        Long station4 = StationRequest.createStationThenReturnId("동두천중앙역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "1호선", "bg-blue-600", station1, station2, 10L
        );
        LineRequest.addSection(createdLineId, station2, station3, 10L);
        LineRequest.addSection(createdLineId, station3, station4, 10L);

        // when
        ExtractableResponse<Response> response = LineRequest.deleteSection(createdLineId, station2);
        ExtractableResponse<Response> getResponse = LineRequest.getLineById(createdLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(station1, station3, station4);
    }

    /**
     * Given 2개 이상의 구간을 가진 지하철 노선을 생성하고
     * When 상행, 하행의 종점을 삭제하면
     * Then 해당 구간은 삭제가 되고 해당 구간과 연결된 구간이 새로운 상행, 하행의 종점이 되어야 한다
     */
    @DisplayName("노선의 상/하행 종점을 삭제하면 삭제되는 구간과 연결된 구간이 새로운 상/하행 종점이 되어야 한다")
    @Test
    void deleteFirstOrLastSection() {
        // given
        Long station1 = StationRequest.createStationThenReturnId("배방역");
        Long station2 = StationRequest.createStationThenReturnId("신창역");
        Long station3 = StationRequest.createStationThenReturnId("온양온천역");
        Long createdLineId1 = LineRequest.createLineThenReturnId(
                "1호선", "bg-blue-600", station1, station3, 10L
        );
        Long createdLineId2 = LineRequest.createLineThenReturnId(
                "1호선", "bg-blue-600", station3, station1, 10L
        );
        LineRequest.addSection(createdLineId1, station1, station2, 5L);
        LineRequest.addSection(createdLineId2, station3, station2, 5L);

        // when
        ExtractableResponse<Response> deleteFirstSectionResponse = LineRequest.deleteSection(createdLineId1, station1);
        ExtractableResponse<Response> deleteLastSectionResponse = LineRequest.deleteSection(createdLineId2, station1);
        ExtractableResponse<Response> getDeleteFirstResponse = LineRequest.getLineById(createdLineId1);
        ExtractableResponse<Response> getDeleteLastResponse = LineRequest.getLineById(createdLineId2);

        // then
        assertThat(deleteFirstSectionResponse.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(deleteLastSectionResponse.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(getDeleteFirstResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(station2, station3);
        assertThat(getDeleteLastResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(station3, station2);
    }

    /**
     * Given 구간이 1개인 지하철 노선을 생성하고
     * When 마지막 구간을 삭제하면
     * Then 해당 삭제는 실패하고 예외가 발생해야 한다
     */
    @DisplayName("하나의 구간만 존재하는 노선의 구간을 삭제하면 예외가 발생하면서 실패해야 한다")
    @Test
    void deleteOneRemainingSection() {
        // given
        Long upStation = StationRequest.createStationThenReturnId("진접역");
        Long downStation = StationRequest.createStationThenReturnId("오남역");
        Long createdLineId = LineRequest.createLineThenReturnId(
                "4호선", "bg-sky-600", upStation, downStation, 10L
        );

        // when
        ExtractableResponse<Response> response = LineRequest.deleteSection(createdLineId, upStation);
        ExtractableResponse<Response> getLineResponse = LineRequest.getLineById(createdLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(getLineResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(upStation, downStation);
    }

//    /**
//     * Given 노선을 생성하고
//     * When 해당 노선에 존재하지 않는 구간을 삭제하면
//     * Then 해당 삭제는 실패하고 예외가 발생해야 한다
//     */
//    @DisplayName("노선에 존재하지 않는 구간을 삭제하면 예외가 발생하면서 실패해야 한다")
//    @Test
//    void deleteNotIncludeSection() {
//        // given
//        Long upStation = StationRequest.createStationThenReturnId("당고개역");
//        Long downStation = StationRequest.createStationThenReturnId("노원역");
//        Long station = StationRequest.createStationThenReturnId("상계역");
//        Long createdLineId = LineRequest.createLineThenReturnId(
//                "4호선", "bg-sky-600", upStation, downStation, 10L
//        );
//
//        // when
//        ExtractableResponse<Response> response = LineRequest.deleteSection(createdLineId, station);
//        ExtractableResponse<Response> getLineResponse = LineRequest.getLineById(createdLineId);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
//        assertThat(getLineResponse.jsonPath().getList("stations.id", Long.class))
//                .containsExactly(upStation, downStation);
//    }
}
