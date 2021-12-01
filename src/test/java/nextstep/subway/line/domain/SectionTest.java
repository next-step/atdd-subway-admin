package nextstep.subway.line.domain;

import nextstep.subway.line.application.exception.InvalidDistanceException;
import nextstep.subway.line.application.exception.InvalidSectionException;
import nextstep.subway.line.application.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nextstep.subway.line.domain.Distance.*;
import static nextstep.subway.line.domain.Section.SECTION_DUPLICATION;
import static nextstep.subway.line.domain.Sections.NOT_CONNECTABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간")
class SectionTest {

    private List<Station> stations;
    private Line line;

    @BeforeEach
    void setUp() {
        stations = Station.of("강남", "양재", "양재시민의숲", "청계산입구", "판교");
        line = Line.of("신분당선", "bg-red-600");
    }

    @DisplayName("종점역을 연장한 구간 추가")
    @Test
    void connectTerminusExtend() {
        // given
        line.addSections(generateSections(stations, 5));
        Section upStationExtend = getSection(Station.of("상행연장", "강남"), 2);
        Section downStationExtend = getSection(Station.of("판교", "하행연장"), 3);

        // when
        line.addSection(upStationExtend);
        line.addSection(downStationExtend);
        List<Section> result = line.getSections();

        // then
        assertThat(result).hasSize(6);
        assertThat(result)
                .extracting("distance")
                .containsExactly(2, 5, 5, 5, 5, 3);
    }

    @DisplayName("구간 사이에 새로운 구간을 추가")
    @Test
    void connectBetweenStations() {
        // given
        Section section = getSection(Station.of("강남", "청계산입구"), 9);
        Section newUpSection = getSection(Station.of("강남", "양재"), 3);
        Section newDownSection = getSection(Station.of("양재시민의숲", "청계산입구"), 4);
        line.addSection(section);

        // when
        line.addSection(newUpSection);
        line.addSection(newDownSection);
        List<Section> result = line.getSections();

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("distance")
                .containsExactly(3, 2, 4);
        assertThat(line.getStationInOrder())
                .extracting("name")
                .containsExactly("강남", "양재", "양재시민의숲", "청계산입구");
    }

    @Test
    @DisplayName("상행역 또는 하행역이 없는 경우 예외 발생")
    void validateNotConnectable() {
        // given
        Section section1 = getSection(Station.of("강남", "양재"), 5);
        Section section2 = getSection(Station.of("판교", "광교"), 5);
        line.addSection(section1);

        // when // then
        assertThatThrownBy(() -> line.addSection(section2))
                .withFailMessage(NOT_CONNECTABLE)
                .isInstanceOf(SectionNotFoundException.class);
    }

    @Test
    @DisplayName("구간이 중복되는 경우 예외 발생")
    void validateSectionDuplication() {
        // given
        List<Station> stations = Station.of("강남", "양재", "강남", "양재");
        List<Section> sections = generateSections(stations, 3);

        // when // then
        assertThatThrownBy(() -> line.addSections(sections))
                .withFailMessage(SECTION_DUPLICATION)
                .isInstanceOf(InvalidSectionException.class);
    }

    @Test
    @DisplayName("구간 거리가 " + MIN_DISTANCE + "이하인 경우 예외 발생")
    void validateShortMinDistance() {
        // given
        List<Station> stations = Station.of("강남", "양재");

        // when // then
        assertThatThrownBy(() -> getSection(stations, MIN_DISTANCE))
                .withFailMessage(SHORT_MIN_DISTANCE)
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("구간 거리가 기존 역 사이의 거리보다 크거나 같은 경우 예외 발생")
    @ParameterizedTest(name = "[{index}]구간 사이 거리")
    @ValueSource(ints = {5, 6})
    void validateLongDistanceBetweenSection(int invalidDistance) {
        // given
        Section section1 = getSection(Station.of("강남", "양재시민의숲"), 5);
        Section section2 = getSection(Station.of("양재", "양재시민의숲"), invalidDistance);
        line.addSection(section1);


        // when // then
        assertThatThrownBy(() -> line.addSection(section2))
                .withFailMessage(LONG_DISTANCE_BETWEEN_SECTION)
                .isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    @DisplayName("구간 목록을 상행역 부터 하행역 순으로 정렬")
    void sortedSections() {
        // given
        List<Section> sections = generateSections(stations, 4);

        // when
        line.addSections(sections);

        // then
        assertThat(line.getStationInOrder())
                .extracting("name")
                .containsExactly("강남", "양재", "양재시민의숲", "청계산입구", "판교");
    }

    private Section getSection(List<Station> stations, int distance) {
        return Section.of(stations.get(0), stations.get(1), distance);
    }

    private List<Section> generateSections(List<Station> stations, int distance) {
        return IntStream.range(0, stations.size() - 1)
                .mapToObj(i -> Section.of(stations.get(i), stations.get(i + 1), distance))
                .collect(Collectors.toList());
    }
}
