package nextstep.subway.station;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("저장 조회")
    void saveSubwaysWithLine() {
        final Line line = lineRepository.save(new Line("2호선", "green darken-1"));
        final Station station1 = new Station("강남역");
        station1.setLine(line);
        stationRepository.save(station1);
        final Station station2 = new Station("역삼역");
        station2.setLine(line);
        stationRepository.save(station2);

    }
}
