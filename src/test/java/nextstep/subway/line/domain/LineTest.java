package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class LineTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @DisplayName("역목록은 상행역 부터 하행역 순으로 정렬 되어야 한다.")
    @Test
    void getStationsBySort() {
        // given
        final Station firstStation = stationRepository.save(Station.of("1번"));
        final Station secondStation = stationRepository.save(Station.of("2번"));
        final Station thirdStation = stationRepository.save(Station.of("3번"));
        final Station forthStation = stationRepository.save(Station.of("4번"));
        final Station fifthStation = stationRepository.save(Station.of("5번"));
        final Line line = lineRepository.save(Line.of("노선이름", "색상", Arrays.asList(
                Section.of(10, thirdStation, forthStation),
                Section.of(10, forthStation, fifthStation),
                Section.of(10, firstStation, secondStation),
                Section.of(10, secondStation, thirdStation)
        )));
        // when
        final List<Station> stationsOrderByUptoDown = line.getStationsOrderByUptoDown();
        // then
        assertThat(stationsOrderByUptoDown).containsExactlyElementsOf(Arrays.asList(firstStation, secondStation, thirdStation, forthStation, fifthStation));
    }

    @DisplayName("등록된 역 사이의 추가될 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정된다.")
    @Test
    void addSectionWithInnerSection() {
        // given
        final Station firstStation = stationRepository.save(Station.of("1번"));
        final Station secondStation = stationRepository.save(Station.of("2번"));
        final Station thirdStation = stationRepository.save(Station.of("3번"));
        final Station forthStation = stationRepository.save(Station.of("4번"));
        final int totalDistance = 12;
        final int newDistance = 4;
        final int newDistance2 = 2;
        Line line = lineRepository.save(Line.of("노선이름", "색상", Section.of(totalDistance, firstStation, forthStation)));
        // when
        line.addSection(Section.of(newDistance, firstStation, secondStation));
        line.addSection(Section.of(newDistance2, secondStation, thirdStation));
        // then
        final List<Section> sections = line.getSections();
        final List<Station> stationsOrderByUptoDown = line.getStationsOrderByUptoDown();
        assertAll(() -> {
            assertThat(sections.get(0).matchDistance(totalDistance - newDistance - newDistance2)).isTrue();
            assertThat(sections.get(1).matchDistance(newDistance)).isTrue();
            assertThat(sections.get(2).matchDistance(newDistance2)).isTrue();
            assertThat(stationsOrderByUptoDown).containsExactlyElementsOf(Arrays.asList(firstStation, secondStation, thirdStation, forthStation));
        });
    }


    @DisplayName("등록된 역 바깥쪽으로 지하철 역 구간을 추가할 수 있다.")
    @Test
    void addSectionWithOuterSection() {
        // given
        final Station firstStation = stationRepository.save(Station.of("1번"));
        final Station secondStation = stationRepository.save(Station.of("2번"));
        final Station thirdStation = stationRepository.save(Station.of("3번"));
        final Station forthStation = stationRepository.save(Station.of("4번"));
        final int totalDistance = 12;
        final int newDistance = 4;
        final int newDistance2 = 2;
        Line line = lineRepository.save(Line.of("노선이름", "색상", Section.of(totalDistance, secondStation, thirdStation)));
        // when
        line.addSection(Section.of(newDistance, firstStation, secondStation));
        line.addSection(Section.of(newDistance2, thirdStation, forthStation));
        // then
        final List<Section> sections = line.getSections();
        final List<Station> stationsOrderByUptoDown = line.getStationsOrderByUptoDown();
        assertAll(() -> {
            assertThat(sections.get(0).matchDistance(totalDistance)).isTrue();
            assertThat(sections.get(1).matchDistance(newDistance)).isTrue();
            assertThat(sections.get(2).matchDistance(newDistance2)).isTrue();
            assertThat(stationsOrderByUptoDown).containsExactlyElementsOf(Arrays.asList(firstStation, secondStation, thirdStation, forthStation));
        });
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void removeWithOuter() {
        // given
        final Station firstStation = stationRepository.save(Station.of("1번"));
        final Station secondStation = stationRepository.save(Station.of("2번"));
        final Station thirdStation = stationRepository.save(Station.of("3번"));
        final Line line = lineRepository.save(Line.of("노선이름", "색상", Arrays.asList(
                Section.of(10, firstStation, secondStation),
                Section.of(10, secondStation, thirdStation)
        )));
        // when
        line.removeStation(firstStation);
        // then
        assertEquals(line.getStationsOrderByUptoDown().get(0), secondStation);
    }

    @DisplayName("중간역이 제거될 경우 재배치를 함 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨 거리는 두 구간의 거리의 합으로 정함")
    @Test
    void removeWithInner() {
        // given
        final Station firstStation = stationRepository.save(Station.of("1번"));
        final Station secondStation = stationRepository.save(Station.of("2번"));
        final Station thirdStation = stationRepository.save(Station.of("3번"));
        final int firstSectionDistance = 10;
        final int secondSectionDistance = 10;
        final Line line = lineRepository.save(Line.of("노선이름", "색상", Arrays.asList(
                Section.of(firstSectionDistance, firstStation, secondStation),
                Section.of(secondSectionDistance, secondStation, thirdStation)
        )));
        // when
        line.removeStation(secondStation);
        // then
        final Section section = line.getSections().get(0);
        assertThat(section.matchDistance(firstSectionDistance + secondSectionDistance)).isTrue();
    }
}