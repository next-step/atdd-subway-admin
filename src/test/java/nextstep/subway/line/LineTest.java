package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @Test
    @DisplayName("처음 구간 추가 하면 upStation이 비어있는 구간을 생성한다.")
    public void insertSectionTest(){

        StationRequest dangSanStationRequest = new StationRequest("당산역");
        StationRequest hongDaeStationRequest = new StationRequest("홍대입구역");
        Station savedDangSanStation = dangSanStationRequest.toStation();
        Station savedHongDaeStation = hongDaeStationRequest.toStation();
        Line line = new Line("2호선", "green", savedDangSanStation, savedHongDaeStation, 10);

        Sections sections = line.getSections();
        assertThat(sections.size()).isEqualTo(2);
    }
}
