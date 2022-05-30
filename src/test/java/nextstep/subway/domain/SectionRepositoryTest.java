package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionRepositoryTest {

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
        Section actual = sectionRepository.save(new Section(new Distance(100), upStation, downStation, line));

        // then
        assertThat(actual).isNotNull();
    }
}
