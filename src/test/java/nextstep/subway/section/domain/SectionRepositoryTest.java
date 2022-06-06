package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SectionRepository는")
@DataJpaTest
public class SectionRepositoryTest {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("구간을 저장한다")
    @Test
    void save() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("잠실역");
        Line line = new Line("2호선", upStation, downStation);

        Section actual = sectionRepository.save(new Section(line, upStation, downStation, 10));
        entityManager.clear();

        Section expected = sectionRepository.findById(1L).orElseThrow(IllegalAccessError::new);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getDistance()).isEqualTo(expected.getDistance());
    }
}
