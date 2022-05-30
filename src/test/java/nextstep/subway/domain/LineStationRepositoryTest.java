package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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
        Line line = lineRepository.save(new Line("2호선", "초록", 100));

        // when
        LineStation actual = lineStationRepository.save(new LineStation(station, line));

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 노선에_연결된_지하철() {
        // given
        Station upStation = stationRepository.save(new Station("잠실역"));
        Station downStation = stationRepository.save(new Station("강남역"));
        Line line = lineRepository.save(new Line("2호선", "#009D3E", 100, upStation, downStation));
        lineStationRepository.save(new LineStation(upStation, line));
        lineStationRepository.save(new LineStation(downStation, line));

        // when
        List<LineStation> lineStations = lineStationRepository.findByLine_Id(line.getId());

        // then
        assertThat(lineStations).hasSize(2);
    }
}
