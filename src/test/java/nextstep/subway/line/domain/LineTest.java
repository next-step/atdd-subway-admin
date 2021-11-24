package nextstep.subway.line.domain;

import nextstep.subway.line.application.SectionNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private List<Station> stations;
    private Line line;

    @BeforeEach
    void setUp() {
        stations = Station.of("강남", "양재", "양재시민의숲", "청계산입구", "판교");
        line = Line.of("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("지하철 노선을 상행역 부터 하행역 순으로 정렬한다.")
    void sortedSections() {
        // given
        line.addSections(getSections(stations, line));

        // when
        List<Station> sortedStations = line.getStations();

        // then
        assertThat(sortedStations)
                .extracting("name")
                .containsExactly("강남", "양재", "양재시민의숲", "청계산입구", "판교");
    }

    @Test
    @DisplayName("상행 종점역을 찾는다.")
    void findFirstSection() {
        // given
        line.addSections((getSections(stations, line)));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations.get(0))
                .extracting("name")
                .isEqualTo("강남");
    }

    @Test
    @DisplayName("상행 종점역이 없는 경우 예외가 발생한다.")
    void validateFirstSection() {
        // given
        stations = Station.of("강남", "강남", "양재");
        line.addSections((getSections(stations, line)));

        // when // then
        assertThatThrownBy(line::getStations)
                .withFailMessage("상행 종점 구간을 찾을 수 없습니다.")
                .isInstanceOf(SectionNotFoundException.class);
    }

    @Test
    @DisplayName("종점역이 아닌 노선이 중복 중복되는 경우 예외가 발생한다.")
    void validateBreakSections() {
        // given
        stations = Station.of("강남", "양재", "판교", "판교");
        List<Section> duplicateSections = getSections(stations, line);

        // when // then
        assertThatThrownBy(() -> line.addSections(duplicateSections))
                .withFailMessage("중복되는 노선이 있습니다.")
                .isInstanceOf(IllegalArgumentException.class);
    }

    private List<Section> getSections(List<Station> stations, Line line) {
        List<Section> sections = new ArrayList<>();

        for (int i = 0; i < stations.size() - 1; i++) {
            Section section = Section.of(stations.get(i), stations.get(i + 1), 5, line);
            sections.add(section);
        }

        return sections;
    }
}
