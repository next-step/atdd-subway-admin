package nextstep.subway.line;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    @Test
    @DisplayName("상행종점 추가 충정로역- 당산역- 홍대입구역")
    public void sortTest() {
        Line line = createLine("당산역", "홍대입구역");

        Station upStation = findStation(line, "당산역");
        Station createdStation = new StationRequest("충정로역").toStation();
        Section newSection = new Section(line, createdStation, upStation, new Distance(3));
        line.addSection(newSection);

        checkStationNames(line,"충정로역", "당산역", "홍대입구역");
        checkDistance(line,"충정로역", 3);
    }

    @Test
    @DisplayName("구간 중간 추가 당산역- 충정로역- 홍대입구역")
    public void addMiddleTest() {
        Line line = createLine("당산역", "홍대입구역");

        Station createdStation = new StationRequest("충정로역").toStation();
        Station upStation = findStation(line, "당산역");
        Section newSection = new Section(line, upStation, createdStation, new Distance(2));
        line.addSection(newSection);

        checkStationNames(line,"당산역", "충정로역", "홍대입구역");
        checkDistance(line,"홍대입구역", 8);
    }

    @Test
    @DisplayName("하행종점 중간 추가 당산역- 홍대입구역- 충정로역")
    public void addEndTest() {
        Line line = createLine("당산역", "홍대입구역");

        Station createdStation = new StationRequest("충정로역").toStation();
        Station downStation = findStation(line, "홍대입구역");
        Section newSection = new Section(line, downStation, createdStation, new Distance(1));
        line.addSection(newSection);

        checkStationNames(line,"당산역", "홍대입구역", "충정로역");
        checkDistance(line,"충정로역", 1);
    }

    @Test
    @DisplayName("구간 중간 추가 기존 구간보다 같을 경우 에러처리")
    public void addSameDistanceTest() {
        Line line = createLine("당산역", "홍대입구역");

        Station createdStation = new StationRequest("충정로역").toStation();
        Station upStation = findStation(line, "당산역");
        Section newSection = new Section(line, upStation, createdStation, new Distance(10));

        assertThatThrownBy(() -> {
            line.addSection(newSection);
        }).isInstanceOf(InputDataErrorException.class)
                .hasMessageContaining("[ERROR]거리는 0 이하가 될수 없습니다.");

    }

    private Line createLine(String upStationName, String downStationName) {
        Station upStation = new StationRequest(upStationName).toStation();
        Station downStation = new StationRequest(downStationName).toStation();

        return new Line("2호선", "green", upStation, downStation, new Distance(10));
    }

    private Station findStation(Line line, String stationName) {
        return line.getStations().stream()
                .filter(it -> it.getName().equals(stationName))
                .findFirst()
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));
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

    private void checkStationNames(Line line, String name1, String name2, String name3) {
        assertThat(line.getStations()
                .stream()
                .map(it -> it.getName())).containsExactly(name1, name2, name3);
    }
}
