package nextstep.subway.section;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    public static final Line line3 = new Line("3호선", "orange");
    public static final Line line5 = new Line("5호선", "purple");

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

    public static ExtractableResponse<Response> 지하철_구간_등록되어_있음(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + section.getLine().getId() + "/sections")
            .then().log().all().extract();
    }
}
