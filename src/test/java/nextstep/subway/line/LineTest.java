package nextstep.subway.line;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 라인 테스트")
public class LineTest {

    @Test
    @DisplayName("지하철 목록을 조회한다.")
    void searchStationsTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");
        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(5));
        line.addSection(newSection);

        assertThat(line.getStations().size()).isEqualTo(3);
        checkValidateStationName(line, "당산역", "홍대입구역", "충정로역");
    }

    @Test
    @DisplayName("구간 제거 테스트- 하행종점 제거")
    void removeDownEndStationInLineTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        Section newEwhaSection = new Section(line, ehwaStation, hongDaeStation, new Distance(2));
        line.addSection(newEwhaSection);

        line.removeStation(chungJeongRoStation);

        assertThat(line.getStations().size()).isEqualTo(3);
        checkValidateStationName(line, "당산역", "이대역", "홍대입구역");
    }

    @Test
    @DisplayName("구간 제거 테스트- 상행종점 제거")
    void removeUpEndStationInLineTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        Section newEwhaSection = new Section(line, ehwaStation, hongDaeStation, new Distance(2));
        line.addSection(newEwhaSection);

        line.removeStation(dangSanStation);

        assertThat(line.getStations().size()).isEqualTo(3);
        checkValidateStationName(line,  "이대역", "홍대입구역", "충정로역");
    }

    @Test
    @DisplayName("구간 제거 테스트- 중간역 제거")
    void removeMiddleStationInLineTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        Section newEwhaSection = new Section(line, ehwaStation, hongDaeStation, new Distance(2));
        line.addSection(newEwhaSection);

        line.removeStation(ehwaStation);

        assertThat(line.getStations().size()).isEqualTo(3);
        checkValidateStationName(line,  "당산역", "홍대입구역", "충정로역");
        checkValidateDistance(line, dangSanStation, hongDaeStation, new Distance(10));
    }

    @Test
    @DisplayName("구간에 등록되지 않는 노선을 삭제할때 예외케이스")
    public void removeStationIsNotRegisteredTest(){
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");
        Station ehwaStation = new Station("이대역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        Section newChungJeongRoSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(5));
        line.addSection(newChungJeongRoSection);

        assertThatThrownBy(() -> {
            line.removeStation(ehwaStation);
        }).isInstanceOf(InputDataErrorException.class)
                .hasMessageContaining(InputDataErrorCode.THERE_IS_NOT_STATION_IN_LINE.errorMessage());

    }

    @Test
    @DisplayName("노선에서 하나 남아있는 구간을 삭제할때 예외케이스")
    public void removeLastOneStationTest(){
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");

        Line line = new Line("2호선", "green", dangSanStation, hongDaeStation, new Distance(10));

        assertThatThrownBy(() -> {
            line.removeStation(dangSanStation);
        }).isInstanceOf(InputDataErrorException.class)
                .hasMessageContaining(InputDataErrorCode.THERE_IS_ONLY_ONE_SECTION_IN_LINE.errorMessage());

    }

    private void checkValidateStationName(Line line, String... stationNames) {
        assertThat(line.getOrderedSections().stream()
                .map(it -> it.getName())
                .distinct())
                .containsExactly(stationNames);
    }

    private void checkValidateDistance(Line line, Station upStation, Station downStation, Distance distance) {
        Section foundSection = line.findSection(upStation, downStation);
        assertThat(foundSection.getDistanceNumber()).isEqualTo(distance.getDistance());

    }
}
