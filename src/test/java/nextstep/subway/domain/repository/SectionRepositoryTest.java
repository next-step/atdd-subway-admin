package nextstep.subway.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    TestEntityManager em;

    Station station1 = null;
    Station station2 = null;

    @Test
    void save() {
        Section expected = getSection();
        Section actual = sectionRepository.save(expected);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        Section actual = sectionRepository.save(getSection());
        sectionRepository.deleteById(actual.getId());
        flushAndClear();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> sectionRepository.findById(actual.getId()).get());
    }

    @Test
    void findByLineId() {
        Section expected = sectionRepository.save(getSection());
        flushAndClear();
        List<Section> actual = sectionRepository.findByLineId(expected.getLineId()).get();
        assertThat(actual).hasSize(1);
    }

    private Section getSection() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        return new Section(new Line("신분당선", "bg-red-600"), 10, station1, station2);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
