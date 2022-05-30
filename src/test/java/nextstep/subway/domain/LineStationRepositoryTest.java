package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineStationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    @Test
    void createLineStation() {
        // given
        Station station = stationRepository.save(new Station("잠실역"));
        Line line = lineRepository.save(new Line("2호선", "초록", new Distance(100)));

        // when
        LineStation actual = lineStationRepository.save(new LineStation(station, line));

        // then
        assertThat(actual).isNotNull();
    }
}
