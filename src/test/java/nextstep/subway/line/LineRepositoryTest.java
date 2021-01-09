package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.web.bind.annotation.DeleteMapping;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("편의 메서드 사용 저장 조회")
    void saveSubwaysWithLine() {
        final Line line = lineRepository.save(new Line("2호선", "green darken-1"));
        final Station station1 = new Station("강남역");
        station1.setLine(line);
        stationRepository.save(station1);
        final Station station2 = new Station("역삼역");
        station2.setLine(line);
        stationRepository.save(station2);

        line.addStation(station1);
        line.addStation(station2);
        Line actual = lineRepository.findByName("2호선");
        assertThat(actual.getStations()).hasSize(2);

    }
}
