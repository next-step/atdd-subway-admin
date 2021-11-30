package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("지하철 라인 테스트")
public class LineTest {

    @Test
    @DisplayName("지하철 목록을 조회한다.")
    void searchStationsTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeoungRoStation = new Station("충정로역");
        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newSection = new Section(line, hongDaeStation, chungJeoungRoStation, new Distance(5));

        line.addSection(newSection);
        assertThat(line.getStations().size()).isEqualTo(3);
        assertThat(line.getStations().stream()
                .map(it -> it.getName())
                .distinct())
                .contains("당산역", "홍대입구역", "충정로역");
    }
}
