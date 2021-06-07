package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("구간 저장")
    @Test
    void save() {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("광교");

        // when
        Section persistSection = saveSection(persistUpStation, persistDownStation, 45);
        Section actual = sectionRepository.findById(persistSection.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(persistSection).isSameAs(actual);
        assertThat(persistUpStation).isSameAs(actual.getUpStation());
        assertThat(persistDownStation).isSameAs(actual.getDownStation());
    }

    @DisplayName("구간삭제 - Sections 클래스 활용")
    @Test
    void deleteBySections() {
        // given
        Station persistUpStation = saveStation("왕십리");
        Station persistDownStation = saveStation("수원");
        Station persistUpStation2 = saveStation("을지로입구");
        Station persistDownStation2 = saveStation("신도림");

        Section persistSection = saveSection(persistUpStation, persistDownStation, 45);
        Section persistSection2 = saveSection(persistUpStation2, persistDownStation2, 30);

        // when
        Sections sections = new Sections();
        sections.add(persistSection);
        sections.add(persistSection2);
        sectionRepository.deleteAll(sections);

        // then
        List<Section> expected = sectionRepository.findAll();
        assertThat(expected).isEmpty();
    }

    @DisplayName("구간을 노선에 연결")
    @Test
    void toLine() {
        // given
        Line persistLine = lineRepository.save(new Line("신분당선", "red"));
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("광교");
        Section persistSection = saveSection(persistUpStation, persistDownStation, 45);

        // when
        persistSection.toLine(persistLine);

        // then
        assertThat(persistLine.contains(persistSection)).isTrue();
        assertThat(persistLine.getStations().size()).isEqualTo(2);
    }

    @DisplayName("시착, 종착역 검증 테스트")
    @Test
    void firstAndLastStop() {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("양재");

        Section persistSection = saveSection(persistUpStation, persistDownStation, 10);

        // when
        Sections sections = new Sections();
        sections.add(persistSection);
        Station firstStop = sections.getStations().get(0);
        Station lastStop = sections.getStations().get(1);

        // then
        assertThat(sections.getStations().size()).isEqualTo(2);
        assertThat(firstStop).isSameAs(persistDownStation);
        assertThat(lastStop).isSameAs(persistUpStation);
    }

    @DisplayName("구간 길이변경 - 유효하지 않은 길이")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void modifyDistance_invalid(int param) {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("양재");

        Section persistSection = saveSection(persistUpStation, persistDownStation, 10);

        // when then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> persistSection.modifyDistance(param))
                .withMessageMatching("거리 값은 0 을 초과하는 값이어야 합니다.");
    }

    @DisplayName("구간추가 - 모든 역이 기존 구간에 포함된 경우")
    @Test
    void add_isStationAllContains() {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("양재");
        Section persistSection = saveSection(persistUpStation, persistDownStation, 10);
        Section addPersistSection = saveSection(persistUpStation, persistDownStation, 10);

        // when
        Sections sections = new Sections();
        sections.add(persistSection);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.add(addPersistSection))
                .withMessageMatching("구간에 속한 모든 역이 노선에 포함되어 있습니다. 역 정보를 확인해주세요.");
    }

    @DisplayName("구간추가 - 모든 역이 기존 구간에 포함되지 않은 경우")
    @Test
    void add_isStationNotContains() {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("양재");
        Section persistSection = saveSection(persistUpStation, persistDownStation, 10);

        Station persistUpStation2 = saveStation("판교");
        Station persistDownStation2 = saveStation("정자");
        Section addPersistSection = saveSection(persistUpStation2, persistDownStation2, 5);

        // when
        Sections sections = new Sections();
        sections.add(persistSection);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.add(addPersistSection))
                .withMessageMatching("구간에 속한 모든 역이 노선에 포함되어 있지 않습니다. 역 정보를 확인해주세요.");
    }

    private Station saveStation(String name) {
        Station station = new Station(name);
        return stationRepository.save(station);
    }

    @DisplayName("구간추가 - 구간 맨 앞에 추가")
    @Test
    void addToFirst() {
        // given
        Station persistUpStation = saveStation("강남");
        Station persistDownStation = saveStation("양재");
        Section persistSection = saveSection(persistUpStation, persistDownStation, 10);

        Station persistDownStation2 = saveStation("판교");
        Section persistSection2 = saveSection(persistDownStation, persistDownStation2, 15);

        // when
        Sections sections = new Sections();
        sections.add(persistSection);
        sections.add(persistSection2);

        // then
        assertThat(sections.getStations().size()).isEqualTo(3);
        assertThat(sections.getFirstStation().getName()).isEqualTo("판교");
        assertThat(sections.getLastStation().getName()).isEqualTo("강남");
    }

    private Section saveSection(Station upStation, Station downStation, int distance) {
        Section section = Section.of(upStation, downStation, distance);
        return sectionRepository.save(section);
    }
}