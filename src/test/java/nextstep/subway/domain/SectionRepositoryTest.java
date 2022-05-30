package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    @Test
    void createSection() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("석촌역"));
        Line line = lineRepository.save(new Line("2호선", "초록", 100));

        // when
        Section actual = sectionRepository.save(new Section(100, upStation, downStation, line));

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 노선내_연결정보_가져오기() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line line = lineRepository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));

        sectionRepository.save(new Section(100, upStation, downStation, line));
        entityManager.clear();

        // when
        List<Section> sections = sectionRepository.findByLine_Id(line.getId());

        // then
        assertThat(sections).hasSize(1);
    }
}
