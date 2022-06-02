package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("노선 내 구간 생성")
    @Test
    void createSection() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("석촌역"));
        Line line = lineRepository.save(new Line("2호선", "초록", 100, upStation, downStation));

        // when
        Section actual = sectionRepository.save(new Section(100, upStation, downStation, line));

        // then
        assertThat(actual).isNotNull();
    }
}
