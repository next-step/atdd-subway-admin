package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("구간 정렬이 되었는지 확인 충정로역- 당산역- 홍대입구역")
    public void sortTest() {
        StationRequest dangSanStationRequest = new StationRequest("당산역");
        StationRequest hongDaeStationRequest = new StationRequest("홍대입구역");

        StationRequest chungJeoungRoStationRequest = new StationRequest("충정로역");
        Station savedDangSanStation = dangSanStationRequest.toStation();
        Station savedHongDaeStation = hongDaeStationRequest.toStation();
        Station chungJeoungRoStation = chungJeoungRoStationRequest.toStation();
        Line line = new Line("2호선", "green", savedDangSanStation, savedHongDaeStation, new Distance(10));

        Section newSection = new Section(line, chungJeoungRoStation, savedDangSanStation, new Distance(5));

        line.addSection(newSection);

        Assertions.assertThat(line.getStations()
                .stream()
                .map(it -> it.getName())).contains("충정로역", "당산역", "홍대입구역");

    }
}
