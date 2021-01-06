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

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 방배역;

    private LineResponse line;

    @BeforeEach
    void beforeSetUp() {
        강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);
        방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(StationResponse.class);

        line = LineAcceptanceTest.지하철_노선_생성("2호선", "bg-green-600", 강남역.getId(), 역삼역.getId(), 10)
                .as(LineResponse.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void addSection1() {
        lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 방배역.getId(), 5));

        List<StationResponse> stationResponses = lineService.findLine(line.getId()).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역.getId()),
                () -> assertThat(stationIds.get(1)).isEqualTo(방배역.getId()),
                () -> assertThat(stationIds.get(2)).isEqualTo(역삼역.getId())
        );
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSection2() {
        lineService.addSection(line.getId(), new SectionRequest(방배역.getId(), 강남역.getId(), 5));

        List<StationResponse> stationResponses = lineService.findLine(line.getId()).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(방배역.getId()),
                () -> assertThat(stationIds.get(1)).isEqualTo(강남역.getId()),
                () -> assertThat(stationIds.get(2)).isEqualTo(역삼역.getId())
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addSection3() {
        lineService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 방배역.getId(), 5));

        List<StationResponse> stationResponses = lineService.findLine(line.getId()).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역.getId()),
                () -> assertThat(stationIds.get(1)).isEqualTo(역삼역.getId()),
                () -> assertThat(stationIds.get(2)).isEqualTo(방배역.getId())
        );
    }

    @DisplayName("중간 지하철역 삭제")
    @Test
    void deleteSection1() {
        lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 방배역.getId(), 5));

        lineService.removeSectionByStationId(line.getId(), 방배역.getId());

        List<StationResponse> stationResponses = lineService.findLine(line.getId()).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역.getId()),
                () -> assertThat(stationIds.get(1)).isEqualTo(역삼역.getId())
        );
    }

    @DisplayName("마지막 지하철역 삭제")
    @Test
    void deleteSection2() {
        lineService.addSection(line.getId(), new SectionRequest(역삼역.getId(), 방배역.getId(), 5));

        lineService.removeSectionByStationId(line.getId(), 방배역.getId());

        List<StationResponse> stationResponses = lineService.findLine(line.getId()).getStationResponses();
        List<Long> stationIds = StationAcceptanceTest.응답_데이터에서_지하철역_id들_추출(stationResponses);
        assertAll(
                () -> assertThat(stationIds.get(0)).isEqualTo(강남역.getId()),
                () -> assertThat(stationIds.get(1)).isEqualTo(역삼역.getId())
        );
    }
}
