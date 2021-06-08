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
        ExtractableResponse<Response> createResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation.getId(), chungjeongnoStation.getId(), 10));
        Long lineId = createResponse.jsonPath().getLong("id");

        // when
        Section section = new Section(lineId, chungjeongnoStation.getId(), seodaemunStation.getId(), 4);
        ExtractableResponse<Response> sectionResponse = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.jsonPath().getLong("upStationId")).isEqualTo(section.getUpStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("downStationId")).isEqualTo(section.getDownStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("distance")).isEqualTo(section.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록 (상행역기준)")
    @Test
    void createSectionBetweenStationsByUpStation() {
        // given
        Section givenSection = new Section(aeogaeStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation.getId(), chungjeongnoStation.getId(), 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findLineResponse = get("/lines/" + lineResponse.getId() + "/sections");
        List<SectionResponse> sectionResponses = findLineResponse.jsonPath().getList(".", SectionResponse.class);
        assertThat(sectionResponses).hasSize(2);
        int totalDistance = findLineResponse.jsonPath().getList(".", SectionResponse.class).stream()
            .mapToInt(SectionResponse::getDistance)
            .sum();
        assertThat(totalDistance).isEqualTo(7);
    }

    @DisplayName("역 사이에 새로운 역을 등록 (하행역기준)")
    @Test
    void createSectionBetweenStationsByDownStation() {
        // given
        Section givenSection = new Section(aeogaeStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), chungjeongnoStation.getId(), seodaemunStation.getId(), 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findLineResponse = get("/lines/" + lineResponse.getId() + "/sections");
        List<SectionResponse> sectionResponses = findLineResponse.jsonPath().getList(".", SectionResponse.class);
        assertThat(sectionResponses).hasSize(2);
        int totalDistance = findLineResponse.jsonPath().getList(".", SectionResponse.class).stream()
            .mapToInt(SectionResponse::getDistance)
            .sum();
        assertThat(totalDistance).isEqualTo(7);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void createNewStationAsUpStation() {
        // given
        Section givenSection = new Section(chungjeongnoStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation.getId(), chungjeongnoStation.getId(), 4);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findLineResponse = get("/lines/" + lineResponse.getId() + "/sections");
        List<SectionResponse> sectionResponses = findLineResponse.jsonPath().getList(".", SectionResponse.class);
        assertThat(sectionResponses).hasSize(2);
        int totalDistance = findLineResponse.jsonPath().getList(".", SectionResponse.class).stream()
            .mapToInt(SectionResponse::getDistance)
            .sum();
        assertThat(totalDistance).isEqualTo(11);

    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void createNewStationAsDownStation() {
        // given
        Section givenSection = new Section(chungjeongnoStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), seodaemunStation.getId(), gwanghwamunStation.getId(), 3);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findLineResponse = get("/lines/" + lineResponse.getId() + "/sections");
        List<SectionResponse> sectionResponses = findLineResponse.jsonPath().getList(".", SectionResponse.class);
        assertThat(sectionResponses).hasSize(2);
        int totalDistance = findLineResponse.jsonPath().getList(".", SectionResponse.class).stream()
            .mapToInt(SectionResponse::getDistance)
            .sum();
        assertThat(totalDistance).isEqualTo(10);
    }

    @DisplayName("예외케이스1 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void case1FailsWhenCreateSection() {
        // given
        Section givenSection = new Section(aeogaeStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation.getId(), chungjeongnoStation.getId(), 7);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외케이스2 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void case2FailsWhenCreateSection() {
        // given
        Section givenSection = new Section(aeogaeStation.getId(), seodaemunStation.getId(), 7);
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, givenSection).as(LineResponse.class);

        // when
        Section section = new Section(lineResponse.getId(), aeogaeStation.getId(), seodaemunStation.getId(), 7);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외케이스3 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void case3FailsWhenCreateSection() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceTest.지하철_노선_등록되어_있음_두_종점역_포함(LineAcceptanceTest.line5, new Section(aeogaeStation.getId(), gwanghwamunStation.getId(), 10));
        Long lineId = createResponse.jsonPath().getLong("id");
        지하철_구간_등록되어_있음(new Section(lineId, aeogaeStation.getId(), chungjeongnoStation.getId(), 3));
        지하철_구간_등록되어_있음(new Section(lineId, chungjeongnoStation.getId(), seodaemunStation.getId(), 4));

        // when
        Station yeouidoStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.yeouidoStation).as(Station.class);
        Station yeouinaruStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.yeouinaruStation).as(Station.class);
        ExtractableResponse<Response> response = 지하철_구간_등록되어_있음(new Section(lineId, yeouidoStation.getId(), yeouinaruStation.getId(), 7));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_구간_등록되어_있음(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        return post(params, "/lines/" + section.getLine().getId() + "/sections");
    }
}
