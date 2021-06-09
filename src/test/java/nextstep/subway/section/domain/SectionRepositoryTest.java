package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DataJpaTest
public class SectionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void save() {
        // given
        final Station upStation = testEntityManager.persist(new Station("강남역"));
        final Station downStation = testEntityManager.persist(new Station("역삼역"));
        final Section expected = new Section(upStation, downStation, 100, new Line("1호선", "blue"));

        // when
        final Section actual = sectionRepository.save(expected);

        // then
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual).isEqualTo(expected)
        );
    }
}
