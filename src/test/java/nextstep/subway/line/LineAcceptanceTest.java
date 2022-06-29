package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.ResponseAssertTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineAcceptanceTest extends BaseAcceptanceTest {
    static final String rootPath = "/lines";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // then
        ResponseAssertTest.생성_확인(createResponse);

        // then
        ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();
        노선_포함_확인(getResponse, new String[]{"7호선"});
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
        지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);
        지하철노선_생성_요청("신분당선", "red", "강남역", "논현역", 15L);

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();

        // then
        노선_포함_확인(getResponse, new String[]{"7호선", "신분당선"});
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // when
        Long id = createResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> getResponse = 지하철노선_단건조회_요청(id);

        // then
        노선_조회_확인(getResponse, id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // when
        Long id = createResponse.jsonPath().getLong("id");
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("1호선", "blue");
        ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(id, lineUpdateRequest);

        // then
        노선_수정_확인(id, updateResponse, lineUpdateRequest);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // when
        Long id = createResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(id);

        // then
        노선_삭제_확인(id, deleteResponse);
    }

    /**
     * When 지하철노선 상행선에 구간을 추가하면
     * Then 노선에 구간이 등록된다.
     */
    @DisplayName("지하철노선 상행선에 구간을 추가한다.")
    @Test
    void createSectionByUpStation() {
        // when
        ExtractableResponse<Response> createResponse = 상행선에_구간추가_요청("도봉산역", "수락산역", 10L);

        // then
        ResponseAssertTest.생성_확인(createResponse);
    }

    /**
     * When 지하철노선 하행선에 구간을 추가하면
     * Then 노선에 구간이 등록된다.
     */
    @DisplayName("지하철노선 하행선에 구간을 추가한다.")
    @Test
    void createSectionByDownStation() {
        // when
        ExtractableResponse<Response> createResponse = 하행선에_구간추가_요청("마들역", "노원역", 10L);

        // then
        ResponseAssertTest.생성_확인(createResponse);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행선 사이에 구간을 추가하면
     * Then 노선에 구간이 등록된다.
     */
    @DisplayName("지하철노선 상행선 사이에 구간을 추가한다.")
    @Test
    void createSectionByBetween() {
        // given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "노원역", 5L);

        // when
        JsonPath jsonPath = createLine.jsonPath();
        ExtractableResponse<Response> createSection = 상행선_사이에_구간추가_요청(jsonPath.getLong("id"), jsonPath.getLong("stations[0].id"), "마들역", 2L);

        // then
        ResponseAssertTest.생성_확인(createSection);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 노선에 이미 등록된 구간을 추가하면
     * Then 구간 추가가 불가하다.
     */
    @DisplayName("상행역, 하행역이 이미 노선에 등록되어 있으면 구간 추가가 불가하다.")
    @Test
    void createSectionErrorByExistsSection() {
        // given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // when
        JsonPath jsonPath = createLine.jsonPath();
        ExtractableResponse<Response> createSection = 지하철노선_구간추가_요청(jsonPath.getLong("id"), jsonPath.getLong("stations[0].id"), jsonPath.getLong("stations[1].id"), 3L);

        // then
        ResponseAssertTest.요청오류_확인(createSection);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 상행역 하행역 둘 중 하나에도 포함되어있지 않으면
     * Then 구간 추가가 불가하다.
     */
    @DisplayName("구간 추가 시 상행역 하행역 둘 중 하나에도 포함되어있지 않으면 구간 추가가 불가하다.")
    @Test
    void createSectionErrorByInvalidStation() {
        // given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // when
        JsonPath jsonPath = createLine.jsonPath();
        ExtractableResponse<Response> createSection = 역이름으로_구간추가_요청(jsonPath.getLong("id"), "공릉역", "태릉입구역", 3L);

        // then
        ResponseAssertTest.요청오류_확인(createSection);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성된 노선 사이에 구간을 추가할 때 기존 거리보다 신규 구간의 거리가 크거나 같으면
     * Then 구간 추가가 불가하다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 거리가 크거나 같으면 구간 추가가 불가하다.")
    @Test
    void createSectionErrorByInvalidDistance() {
        // given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "노원역", 5L);

        // when
        JsonPath jsonPath = createLine.jsonPath();
        ExtractableResponse<Response> createSection = 상행선_사이에_구간추가_요청(jsonPath.getLong("id"), jsonPath.getLong("stations[0].id"), "마들역", 5L);

        // then
        ResponseAssertTest.요청오류_확인(createSection);
    }

    /**
     * Given 3개의 역이 있는 노선을 생성하고 (수락산역-5-마들역-5-노원역)
     * When 상행 종점역을 삭제하면 (수락산역 제거)
     * Then 2개 역이 남아있는 노선을 확인할 수 있다. (마들역-5-노원역)
     */
    @DisplayName("상행 종점역을 제거한다.")
    @Test
    void removeUpStationSection() {
        // Given
        ExtractableResponse<Response> createLine = 지하철노선_구간생성_요청("7호선", "green", Arrays.asList("수락산역", "마들역", "노원역"), 5L);

        // When
        Long lineId = createLine.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteSection = 지하철노선_구간제거_요청(lineId, createLine.jsonPath().getLong("stations[0].id"));

        // Then
        assertAll(
            () -> ResponseAssertTest.성공_확인(deleteSection),
            () -> 지하철노선_삭제후_구간확인(lineId, new String[]{"마들역", "노원역"})
        );
    }

    /**
     * Given 3개의 역이 있는 노선을 생성하고 (수락산역-5-마들역-5-노원역)
     * When 하행 종점역을 삭제하면 (노원역 제거)
     * Then 2개 역이 남아있는 노선을 확인할 수 있다. (수락산역-5-마들역)
     */
    @DisplayName("하행 종점역을 제거한다.")
    @Test
    void removeDownStationSection() {
        // Given
        ExtractableResponse<Response> createLine = 지하철노선_구간생성_요청("7호선", "green", Arrays.asList("수락산역", "마들역", "노원역"), 5L);

        // When
        Long lineId = createLine.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteSection = 지하철노선_구간제거_요청(lineId, createLine.jsonPath().getLong("stations[2].id"));

        // Then
        assertAll(
            () -> ResponseAssertTest.성공_확인(deleteSection),
            () -> 지하철노선_삭제후_구간확인(lineId, new String[]{"수락산역", "마들역"})
        );
    }

    /**
     * Given 3개의 역이 있는 노선을 생성하고 (수락산역-5-마들역-5-노원역)
     * When 중간 역을 삭제하면 (마들역 제거)
     * Then 2개 역이 남아있고, 노선이 재배치되며 거리는 두 구간의 합으로 변경된다. (수락산역-10-노원역)
     */
    @DisplayName("중간 역을 제거하면 노선이 재배치된다.")
    @Test
    void removeBetweenSection() {
        // Given
        ExtractableResponse<Response> createLine = 지하철노선_구간생성_요청("7호선", "green", Arrays.asList("수락산역", "마들역", "노원역"), 5L);

        // When
        Long lineId = createLine.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteSection = 지하철노선_구간제거_요청(lineId, createLine.jsonPath().getLong("stations[1].id"));

        // Then
        assertAll(
            () -> ResponseAssertTest.성공_확인(deleteSection),
            () -> 지하철노선_삭제후_구간확인(lineId, new String[]{"수락산역", "노원역"})
        );
    }

    /**
     * Given 2개의 역이 있는 노선을 생성하고 (수락산역-5-마들역)
     * When 구간이 1개인 노선에 역을 제거하려고 하면 (수락산역 제거)
     * Then 제거할 수 없다.
     */
    @DisplayName("구간이 1개인 노선은 제거할 수 없다.")
    @Test
    void invalidRemoveSection() {
        // Given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);

        // When
        Long lineId = createLine.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteSection = 지하철노선_구간제거_요청(lineId, createLine.jsonPath().getLong("stations[0].id"));

        // Then
        ResponseAssertTest.요청오류_확인(deleteSection);
    }

    /**
     * Given 2개의 역이 있는 노선을 생성하고 (수락산역-5-마들역)
     * When 구간에 포함되지 않은 역을 제거하려고 하면 (노원역 제거)
     * Then 제거할 수 없다.
     */
    @DisplayName("구간에 포함되지 않은 역은 제거할 수 없다.")
    @Test
    void RemoveNotExistsSection() {
        // Given
        ExtractableResponse<Response> createLine = 지하철노선_생성_요청("7호선", "green", "수락산역", "마들역", 5L);
        Long notIncludeStationId = StationAcceptanceTest.지하철역_생성_요청("노원역").jsonPath().getLong("id");

        // When
        Long lineId = createLine.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteSection = 지하철노선_구간제거_요청(lineId, notIncludeStationId);

        // Then
        ResponseAssertTest.요청오류_확인(deleteSection);
    }

    private void 지하철노선_삭제후_구간확인(Long lineId, String[] stations) {
        ExtractableResponse<Response> findLine = 지하철노선_단건조회_요청(lineId);
        assertAll(
            () -> ResponseAssertTest.성공_확인(findLine),
            () -> assertThat(findLine.jsonPath().getList("stations").size()).isEqualTo(stations.length),
            () -> {
                List<String> findStationNames = findLine.jsonPath().getList("stations.name");
                assertThat(findStationNames).containsExactlyInAnyOrder(stations);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_구간생성_요청(String name, String color, List<String> stations, Long distance) {
        ExtractableResponse<Response> createLineResponse = 지하철노선_생성_요청(name, color, stations.get(0), stations.get(1), distance);
        Long newDownStationId = StationAcceptanceTest.지하철역_생성_요청(stations.get(2)).jsonPath().getLong("id");

        return 지하철노선_구간추가_요청(createLineResponse.jsonPath().getLong("id"), createLineResponse.jsonPath().getLong("stations[1].id"), newDownStationId, distance);
    }

    private ExtractableResponse<Response> 상행선_사이에_구간추가_요청(Long lineId, Long upStationId, String downStationName, Long distance) {
        Long newDownStationId = StationAcceptanceTest.지하철역_생성_요청(downStationName).jsonPath().getLong("id");

        return 지하철노선_구간추가_요청(lineId, upStationId, newDownStationId, distance);
    }

    private ExtractableResponse<Response> 상행선에_구간추가_요청(String newUpStationName, String newDownStationName, Long distance) {
        ExtractableResponse<Response> createLineResponse = 지하철노선_생성_요청("7호선", "green", newDownStationName, "마들역", 5L);
        Long lineId = createLineResponse.jsonPath().getLong("id");
        Long upStationId = createLineResponse.jsonPath().getLong("stations[0].id");

        Long newUpStationId = StationAcceptanceTest.지하철역_생성_요청(newUpStationName).jsonPath().getLong("id");

        return 지하철노선_구간추가_요청(lineId, newUpStationId, upStationId, distance);
    }

    private ExtractableResponse<Response> 하행선에_구간추가_요청(String newUpStationName, String newDownStationName, Long distance) {
        ExtractableResponse<Response> createLineResponse = 지하철노선_생성_요청("7호선", "green", "수락산역", newUpStationName, 5L);
        Long lineId = createLineResponse.jsonPath().getLong("id");
        Long downStationId = createLineResponse.jsonPath().getLong("stations[1].id");

        Long newDownStationId = StationAcceptanceTest.지하철역_생성_요청(newDownStationName).jsonPath().getLong("id");

        return 지하철노선_구간추가_요청(lineId, downStationId, newDownStationId, distance);
    }

    private ExtractableResponse<Response> 역이름으로_구간추가_요청(Long lineId, String upStationName, String downStationName, Long distance) {
        Long upStationId = StationAcceptanceTest.지하철역_생성_요청(upStationName).jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철역_생성_요청(downStationName).jsonPath().getLong("id");

        return 지하철노선_구간추가_요청(lineId, upStationId, downStationId, distance);
    }

    private ExtractableResponse<Response> 지하철노선_구간추가_요청(Long lineId, Long upStationId, Long downStationId, Long distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(rootPath + "/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_구간제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(rootPath + "/" + lineId + "/sections?stationId=" + stationId)
            .then().log().all()
            .extract();
    }

    private void 노선_삭제_확인(Long id, ExtractableResponse<Response> response) {
        assertAll(
            () -> ResponseAssertTest.빈응답_확인(response),
            () -> {
                ExtractableResponse<Response> getResponse = 지하철노선_목록조회_요청();

                assertThat(getResponse.jsonPath().getList("id")).doesNotContain(id);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_수정_확인(Long id, ExtractableResponse<Response> response, LineUpdateRequest lineUpdateRequest) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                ExtractableResponse<Response> getResponse = 지하철노선_단건조회_요청(id);

                assertThat(getResponse.jsonPath().getString("name")).isEqualTo(lineUpdateRequest.getName());
                assertThat(getResponse.jsonPath().getString("color")).isEqualTo(lineUpdateRequest.getColor());
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
            .body(lineUpdateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().patch(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_조회_확인(ExtractableResponse<Response> response, Long id) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                assertThat(response.jsonPath().getLong("id")).isEqualTo(id);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_단건조회_요청(long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(rootPath + "/" + id)
            .then().log().all()
            .extract();
    }

    private void 노선_포함_확인(ExtractableResponse<Response> response, String[] lineName) {
        assertAll(
            () -> ResponseAssertTest.성공_확인(response),
            () -> {
                List<String> lineNamesAll = response.jsonPath().getList("name");
                assertThat(lineNamesAll).containsExactlyInAnyOrder(lineName);
            }
        );
    }

    private ExtractableResponse<Response> 지하철노선_목록조회_요청() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(rootPath)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, String upStationName, String downStationName, Long distance) {
        Long upStationId = StationAcceptanceTest.지하철역_생성_요청(upStationName).jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철역_생성_요청(downStationName).jsonPath().getLong("id");

        LineRequest lineRequest = new LineRequest(name, color, distance, upStationId, downStationId);

        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(rootPath)
            .then().log().all()
            .extract();
    }
}
