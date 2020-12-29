


package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("지하철 구간 관련 기능 테스")
public class SectionAcceptanceTest extends AcceptanceTest {
    StationResponse station1;
    StationResponse station2;
    LineResponse line;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
         station1 = StationAcceptanceTest.createStation("강남역").as(StationResponse.class);
         station2 = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);

         line = LineAcceptanceTest
                .createSubwayLine("신분당선", "bg-red-600",
                        station1.getId() + "", station2.getId() + "",
                        10 + "").as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        StationResponse station = StationAcceptanceTest.createStation("판교역").as(StationResponse.class);
        LineAcceptanceTest.addSubwayStation(line.getId(), station2.getId() + "", station.getId() + "", 6 + "");

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.searchSubwayLineOne(line.getId());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo(line.getName());
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .contains(station1.getName(), station2.getName(), station.getName());
    }
}

