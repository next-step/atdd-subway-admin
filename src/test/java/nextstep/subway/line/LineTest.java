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
        checkValidateStationName(line, "당산역", "홍대입구역", "충정로역");
    }

    @Test
    @DisplayName("구간 제거 테스트- 하행종점 제거")
    void removeDownEndStationInLineTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeoungRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeoungRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        Section newEwhaSection = new Section(line, ehwaStation, hongDaeStation, new Distance(2));
        line.addSection(newEwhaSection);

        line.removeStation(chungJeoungRoStation);

        assertThat(line.getStations().size()).isEqualTo(4);
        checkValidateStationName(line, "당산역", "이대역", "홍대입구역", "충정로역");
    }

    @Test
    @DisplayName("구간 제거 테스트- 상행종점 제거")
    void removeUpEndStationInLineTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeoungRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeoungRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        Section newEwhaSection = new Section(line, ehwaStation, hongDaeStation, new Distance(2));
        line.addSection(newEwhaSection);

        line.removeStation(dangSanStation);

        assertThat(line.getStations().size()).isEqualTo(3);
        checkValidateStationName(line,  "이대역", "홍대입구역", "충정로역");
    }

    private void checkValidateStationName(Line line, String... stationNames) {
        assertThat(line.getStations().stream()
                .map(it -> it.getName())
                .distinct())
                .contains(stationNames);
    }
}
