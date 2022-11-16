package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class LineStationRepositoryTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    LineStationRepository lineStationRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    TestEntityManager em;

    Station station1 = null;
    Station station2 = null;

    @Test
    void save() {
        LineStation expected = getLineStation();
        LineStation actual = lineStationRepository.save(expected);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual == expected).isTrue();
    }

    @Test
    void delete() {
        LineStation actual = lineStationRepository.save(getLineStation());
        lineStationRepository.deleteById(actual.getId());
        flushAndClear();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> lineStationRepository.findById(actual.getId()).get());
    }

    @Test
    void findByLineId() {
        LineStation expected = lineStationRepository.save(getLineStation());
        flushAndClear();
        List<LineStation> actual = lineStationRepository.findByLineId(expected.getLineId()).get();
        assertThat(actual).hasSize(1);
    }

    private LineStation getLineStation() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        return new LineStation(line, 10, station1, station2);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
