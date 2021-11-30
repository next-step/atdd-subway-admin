package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    @Test
    @DisplayName("지하철 목록을 조회한다.")
    void searchStationsTest() {
        StationRequest dangSanStationRequest = new StationRequest("당산역");
        StationRequest hongDaeStationRequest = new StationRequest("홍대입구역");

        StationRequest chungJeoungRoStationRequest = new StationRequest("충정로역");
        Station savedDangSanStation = dangSanStationRequest.toStation();
        Station savedHongDaeStation = hongDaeStationRequest.toStation();
        Station chungJeoungRoStation = chungJeoungRoStationRequest.toStation();
        Line line = new Line("2호선", "green", savedDangSanStation, savedHongDaeStation, new Distance(10));

        Section newSection = new Section(line, savedHongDaeStation, chungJeoungRoStation, new Distance(5));

        line.addSection(newSection);
        assertThat(line.getStations().size()).isEqualTo(3);
        assertThat(line.getStations().stream()
                .map(it -> it.getName())
                .distinct())
                .contains("당산역", "홍대입구역", "충정로역");
    }
}
