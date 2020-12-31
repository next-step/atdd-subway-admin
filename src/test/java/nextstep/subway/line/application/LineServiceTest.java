package nextstep.subway.line.application;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineServiceTest extends AcceptanceTest {
    @Autowired
    private LineService lineService;

    private Long 강남역_id;
    private Long 역삼역_id;
    private Long 방배역_id;

    private Long line_id;

    @BeforeEach
    void beforeSetUp() {
        강남역_id = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId();
        역삼역_id = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class).getId();
        방배역_id = StationAcceptanceTest.지하철역_생성("방배역").as(StationResponse.class).getId();

        line_id = LineAcceptanceTest.지하철_노선_생성("2호선", "bg-green-600", 강남역_id, 역삼역_id, 10)
                .as(LineResponse.class).getId();
    }

    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void addSection1() {
        lineService.addSection(line_id, new SectionRequest(강남역_id, 방배역_id, 5));

        List<StationResponse> stationResponses = lineService.findLine(line_id).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역_id),
                () -> assertThat(stationIds.get(1)).isEqualTo(방배역_id),
                () -> assertThat(stationIds.get(2)).isEqualTo(역삼역_id)
        );
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSection2() {
        lineService.addSection(line_id, new SectionRequest(방배역_id, 강남역_id, 5));

        List<StationResponse> stationResponses = lineService.findLine(line_id).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(방배역_id),
                () -> assertThat(stationIds.get(1)).isEqualTo(강남역_id),
                () -> assertThat(stationIds.get(2)).isEqualTo(역삼역_id)
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addSection3() {
        lineService.addSection(line_id, new SectionRequest(역삼역_id, 방배역_id, 5));

        List<StationResponse> stationResponses = lineService.findLine(line_id).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역_id),
                () -> assertThat(stationIds.get(1)).isEqualTo(역삼역_id),
                () -> assertThat(stationIds.get(2)).isEqualTo(방배역_id)
        );
    }
}
