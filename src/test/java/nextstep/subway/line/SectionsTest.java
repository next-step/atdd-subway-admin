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

@DisplayName("지하철 구간 기능 테스트")
public class SectionsTest {

    @Test
    @DisplayName("상행종점 추가 충정로역- 당산역- 홍대입구역")
    public void sortTest() {

        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);
        Section newSection = new Section(line, chungJeongRoStation, dangSanStation, new Distance(3));
        line.addSection(newSection);

        checkStationNames(line, "충정로역", "당산역", "홍대입구역");
        checkDistance(line, "충정로역", 3);
    }

    @Test
    @DisplayName("구간 중간 추가 당산역- 충정로역- 홍대입구역")
    public void addMiddleTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);

        Section newSection = new Section(line, dangSanStation, chungJeongRoStation, new Distance(2));
        line.addSection(newSection);

        checkStationNames(line, "당산역", "충정로역", "홍대입구역");
        checkDistance(line, "홍대입구역", 8);
    }

    @Test
    @DisplayName("하행종점 중간 추가 당산역- 홍대입구역- 충정로역")
    public void addEndTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);

        Section newSection = new Section(line, hongDaeStation, chungJeongRoStation, new Distance(1));
        line.addSection(newSection);

        checkStationNames(line, "당산역", "홍대입구역", "충정로역");
        checkDistance(line, "충정로역", 1);
    }

    @Test
    @DisplayName("구간 중간 추가 기존 구간보다 같을 경우 에러처리")
    public void addSameDistanceTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);

        Section newSection = new Section(line, dangSanStation, chungJeongRoStation, new Distance(10));

        assertThatThrownBy(() -> {
            line.addSection(newSection);
        }).isInstanceOf(InputDataErrorException.class)
                .hasMessageContaining(InputDataErrorCode.DISTANCE_IS_NOT_LESS_THEN_ZERO.errorMessage());

    }

    @Test
    @DisplayName("첫번째 Station 찾기")
    public void searchFirstStationTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);

        Section newSection = new Section(line, dangSanStation, chungJeongRoStation, new Distance(7));
        line.addSection(newSection);

        Station firstStation = line.getFirstStation();
        assertThat(firstStation).isEqualTo(dangSanStation);
    }

    @Test
    @DisplayName("마지막 Station 찾기")
    public void searchLastStationTest() {
        Station dangSanStation = new Station("당산역");
        Station hongDaeStation = new Station("홍대입구역");
        Station chungJeongRoStation = new Station("충정로역");

        Line line = createLine(dangSanStation, hongDaeStation);

        Section newSection = new Section(line, dangSanStation, chungJeongRoStation, new Distance(7));
        line.addSection(newSection);

        Station lastStation = line.getLastStation();
        assertThat(lastStation).isEqualTo(hongDaeStation);
    }

    private Line createLine(Station upStation, Station downStation) {
        return new Line("2호선", "green", upStation, downStation, new Distance(10));
    }

    private void checkDistance(Line line, String sectionStationName, int exepectedDistance) {
        int addedSectionDistance = line.getSections().stream()
                .filter(it -> isSameStationName(sectionStationName, it))
                .map(it -> it.getDistance().getDistance())
                .findFirst().get();
        assertThat(addedSectionDistance).isEqualTo(exepectedDistance);
    }

    private boolean isSameStationName(String sectionStationName, Section it) {
        return it.getDownStation().getName().equals(sectionStationName) || it.getUpStation().getName().equals(sectionStationName);
    }

    private void checkStationNames(Line line, String... stationNames) {
        assertThat(line.getOrderedSections()
                .stream()
                .map(it -> it.getName())).containsExactly(stationNames);
    }
}
