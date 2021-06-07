package nextstep.subway.section;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.section.domain.Section;
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
        ExtractableResponse<Response> createResponse = LineAcceptanceTest.지하철_노선_등록되어_있음(LineAcceptanceTest.line5);
        Long lineId = createResponse.jsonPath().getLong("id");

        // when
        Section section = new Section(lineId, aeogaeStation.getId(), chungjeongnoStation.getId(), 1000);
        ExtractableResponse<Response> sectionResponse = 지하철_구간_등록되어_있음(section);

        // then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(sectionResponse.jsonPath().getLong("upStationId")).isEqualTo(section.getUpStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("downStationId")).isEqualTo(section.getDownStation().getId());
        assertThat(sectionResponse.jsonPath().getLong("distance")).isEqualTo(section.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void createSectionBetweenStations() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-C, 7m)

        // when
        // 지하철 구간 등록 (A-B, 4m)

        // then
        // 지하철 구간이 2개 등록되어 있음
        // A-B, 4m
        // B-C, 3m
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void createNewStationAsUpStation() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-C, 7m)

        // when
        // 지하철 구간 등록 (B-A, 4m)

        // then
        // 지하철 구간이 2개 등록되어 있음
        // B-A, 4m
        // A-C, 7m

    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void createNewStationAsDownStation() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-C, 7m)

        // when
        // 지하철 구간 등록 (C-B, 3m)

        // then
        // 지하철 구간이 2개 등록되어 있음
        // A-C, 7m
        // C-B, 3m
    }

    @DisplayName("예외케이스1 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void case1FailsWhenCreateSection() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-C, 7m)

        // when
        // 지하철 구간 등록 (B-C, 7m)

        // then
        // 예외발생 (등록하려는 구간의 길이보다 크거나 같음)
    }

    @DisplayName("예외케이스2 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void case2FailsWhenCreateSection() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-C, 7m)

        // when
        // 지하철 구간 등록 (A-C, 7m)

        // then
        // 예외발생 (등록하려는 구간의 상행역과 하행선이 모두 등록되어 있음)
    }

    @DisplayName("예외케이스3 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void case3FailsWhenCreateSection() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_구간_등록되어_있음 (A-B, 3m)
        // 지하철_구간_등록되어_있음 (B-C, 4m)

        // when
        // 지하철 구간 등록 (X-Y, 7m)

        // then
        // 예외발생 (상행역과 하행역 둘중 포함되는역이 없음)
    }

    public static ExtractableResponse<Response> 지하철_구간_등록되어_있음(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        return post(params, "/lines/" + section.getLine().getId() + "/sections");
    }
}
