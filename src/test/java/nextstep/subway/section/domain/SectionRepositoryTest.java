package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionRepositoryTest {

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
        Section section = new Section(persistUpStation, persistDownStation, 45);
        Section persistSection = sectionRepository.save(section);
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

    private Station saveStation(String name) {
        Station station = new Station(name);
        return stationRepository.save(station);
    }

    private Section saveSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        return sectionRepository.save(section);
    }
}