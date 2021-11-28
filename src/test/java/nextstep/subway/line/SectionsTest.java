package nextstep.subway.line;

import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Line line;
    private Station savedDangSanStation;
    private Station savedHongDaeStation;
    private Station chungJeoungRoStation;

    @BeforeEach
    void setUp() {
        StationRequest dangSanStationRequest = new StationRequest("당산역");
        StationRequest hongDaeStationRequest = new StationRequest("홍대입구역");

        StationRequest chungJeoungRoStationRequest = new StationRequest("충정로역");
        savedDangSanStation = dangSanStationRequest.toStation();
        savedHongDaeStation = hongDaeStationRequest.toStation();
        chungJeoungRoStation = chungJeoungRoStationRequest.toStation();
        line = new Line("2호선", "green", savedDangSanStation, savedHongDaeStation, new Distance(10));
    }

    @Test
    @DisplayName("상행종점 추가 충정로역- 당산역- 홍대입구역")
    public void sortTest() {
        Section newSection = new Section(line, chungJeoungRoStation, savedDangSanStation, new Distance(3));
        line.addSection(newSection);
        checkStationNames("충정로역", "당산역", "홍대입구역");
        checkDistance("충정로역", 3);
    }

    @Test
    @DisplayName("구간 중간 추가 당산역- 충정로역- 홍대입구역")
    public void addMiddleTest() {
        Section newSection = new Section(line, savedDangSanStation, chungJeoungRoStation, new Distance(2));
        line.addSection(newSection);
        checkStationNames("당산역", "충정로역", "홍대입구역");
        checkDistance("홍대입구역", 8);
    }

    @Test
    @DisplayName("하행종점 중간 추가 당산역- 홍대입구역- 충정로역")
    public void addEndTest() {
        Section newSection = new Section(line, savedHongDaeStation, chungJeoungRoStation, new Distance(1));
        line.addSection(newSection);
        checkStationNames("당산역", "홍대입구역", "충정로역");
        checkDistance("충정로역", 1);
    }

    @Test
    @DisplayName("구간 중간 추가 기존 구간보다 같을 경우 에러처리")
    public void addSameDistanceTest() {
        Section newSection = new Section(line, savedDangSanStation, chungJeoungRoStation, new Distance(10));

        assertThatThrownBy(() -> {
            line.addSection(newSection);
        }).isInstanceOf(InputDataErrorException.class)
                .hasMessageContaining("[ERROR]거리는 0 이하가 될수 없습니다.");

    }

    private void checkDistance(String sectionStationName, int exepectedDistance){
        int addedSectionDistance = line.getSections().stream()
                .filter(it-> isSameStationName(sectionStationName, it))
                .map(it-> it.getDistance().getDistance())
                .findFirst().get();
        assertThat(addedSectionDistance).isEqualTo(exepectedDistance);
    }

    private boolean isSameStationName(String sectionStationName, Section it) {
        return it.getDownStation().getName().equals(sectionStationName) || it.getUpStation().getName().equals(sectionStationName);
    }

    private void checkStationNames(String name1, String name2, String name3) {
        assertThat(line.getStations()
                .stream()
                .map(it -> it.getName())).containsExactly(name1, name2, name3);
    }
}
