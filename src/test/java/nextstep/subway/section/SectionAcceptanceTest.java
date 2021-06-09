package nextstep.subway.section;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Station aeogaeStation;
    private Station chungjeongnoStation;
    private Station seodaemunStation;
    private Station gwanghwamunStation;

    @BeforeEach
    void setup() {
        aeogaeStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.aeogaeStation).as(Station.class);
        chungjeongnoStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.chungjeongnoStation).as(Station.class);
        seodaemunStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.seodaemunStation).as(Station.class);
        gwanghwamunStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.gwanghwamunStation).as(Station.class);
    }

    @DisplayName("지하철 구간 생성")
    @Test
    void createSection() {
        // given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, chungjeongnoStation, 10)).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), chungjeongnoStation, seodaemunStation, 4);
        ExtractableResponse<Response> sectionResponse = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(sectionResponse, HttpStatus.CREATED);
        assertThat(sectionResponse.jsonPath().getLong("upStationId")).isEqualTo(section.getUpStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("downStationId")).isEqualTo(section.getDownStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("distance")).isEqualTo(section.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록 (상행역기준)")
    @Test
    void createSectionBetweenStationsByUpStation() {
        // given
        Section givenSection = new Section(aeogaeStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation, chungjeongnoStation, 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.CREATED);
        assertLineStationSizeAndDistance(lineResponse.getId(), 2, 7);
    }

    @DisplayName("역 사이에 새로운 역을 등록 (하행역기준)")
    @Test
    void createSectionBetweenStationsByDownStation() {
        // given
        Section givenSection = new Section(aeogaeStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), chungjeongnoStation, seodaemunStation, 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.CREATED);
        assertLineStationSizeAndDistance(lineResponse.getId(), 2, 7);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void createNewStationAsUpStation() {
        // given
        Section givenSection = new Section(chungjeongnoStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation, chungjeongnoStation, 4);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.CREATED);
        assertLineStationSizeAndDistance(lineResponse.getId(), 2, 11);

    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void createNewStationAsDownStation() {
        // given
        Section givenSection = new Section(chungjeongnoStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), seodaemunStation, gwanghwamunStation, 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.CREATED);
        assertLineStationSizeAndDistance(lineResponse.getId(), 2, 10);
    }

    @DisplayName("예외케이스1 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void case1FailsWhenCreateSection() {
        // given
        Section givenSection = new Section(aeogaeStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation, chungjeongnoStation, 7);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("예외케이스2 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void case2FailsWhenCreateSection() {
        // given
        Section givenSection = new Section(aeogaeStation, seodaemunStation, 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation, seodaemunStation, 7);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("예외케이스3 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void case3FailsWhenCreateSection() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, gwanghwamunStation, 10));
        Long lineId = createResponse.jsonPath().getLong("id");
        지하철_구간_등록되어_있음(new Section(lineId, aeogaeStation, chungjeongnoStation, 3));
        지하철_구간_등록되어_있음(new Section(lineId, chungjeongnoStation, seodaemunStation, 4));

        // when
        Station yeouidoStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.yeouidoStation).as(Station.class);
        Station yeouinaruStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.yeouinaruStation).as(Station.class);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(new Section(lineId, yeouidoStation, yeouinaruStation, 7));

        // then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("구간에서 중간역 삭제")
    @Test
    void deleteMiddleStation() {
        // Given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, seodaemunStation, 7)).as(LineResponse.class);
        지하철_구간_등록되어_있음(new Section(lineResponse.getId(), aeogaeStation, chungjeongnoStation, 3));

        // When
        ExtractableResponse<Response> response = delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + chungjeongnoStation.getId());

        // Then
        assertResponseCode(response, HttpStatus.NO_CONTENT);
        assertLineStationSizeAndDistance(lineResponse.getId(), 1, 7);
    }

    @DisplayName("구간에서 상행역 삭제")
    @Test
    void deleteUpStation() {
        // Given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, chungjeongnoStation, 3)).as(LineResponse.class);
        지하철_구간_등록되어_있음(new Section(lineResponse.getId(), chungjeongnoStation, seodaemunStation, 4));

        // When
        ExtractableResponse<Response> response = delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + aeogaeStation.getId());

        // Then
        assertResponseCode(response, HttpStatus.NO_CONTENT);
        assertLineStationSizeAndDistance(lineResponse.getId(), 1, 4);
    }

    @DisplayName("구간에서 하행역 삭제")
    @Test
    void deleteDownStation() {
        // Given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, chungjeongnoStation, 3)).as(LineResponse.class);
        지하철_구간_등록되어_있음(new Section(lineResponse.getId(), chungjeongnoStation, seodaemunStation, 4));

        // When
        ExtractableResponse<Response> response = delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + seodaemunStation.getId());

        // Then
        assertResponseCode(response, HttpStatus.NO_CONTENT);
        assertLineStationSizeAndDistance(lineResponse.getId(), 1, 3);
    }

    @DisplayName("노선에 등록되지 않은 역을 삭제 시도시 예외발생")
    @Test
    void failCase1_when_deleteStation() {
        // Given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, chungjeongnoStation, 3)).as(LineResponse.class);
        지하철_구간_등록되어_있음(new Section(lineResponse.getId(), chungjeongnoStation, seodaemunStation, 4));

        // When
        ExtractableResponse<Response> response = delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + gwanghwamunStation.getId());

        // Then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("구간이 1개만 남았을 경우 삭제 시도시 예외발생")
    @Test
    void failCase2_when_deleteStation() {
        // Given
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation, chungjeongnoStation, 3)).as(LineResponse.class);

        // When
        ExtractableResponse<Response> response = delete("/lines/" + lineResponse.getId() + "/sections?stationId=" + aeogaeStation.getId());

        // Then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    public static ExtractableResponse<Response> 지하철_구간_등록되어_있음(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        return post(params, "/lines/" + section.getLine().getId() + "/sections");
    }

    private ExtractableResponse<Response> findLine(Long lineId) {
        return get("/lines/" + lineId + "/sections");
    }

    private List<SectionResponse> findSectionResponses(ExtractableResponse<Response> findLineResponse) {
        return findLineResponse.jsonPath().getList(".", SectionResponse.class);
    }

    private int sumDistancesSections(ExtractableResponse<Response> findLineResponse) {
        return findSectionResponses(findLineResponse).stream()
            .mapToInt(SectionResponse::getDistance)
            .sum();
    }

    void assertLineStationSizeAndDistance(Long lineId, int stationSize, int distance) {
        ExtractableResponse<Response> findLineResponse = findLine(lineId);
        assertThat(findSectionResponses(findLineResponse)).hasSize(stationSize);
        assertThat(sumDistancesSections(findLineResponse)).isEqualTo(distance);
    }
}
