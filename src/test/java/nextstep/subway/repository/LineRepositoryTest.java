package nextstep.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Line line;
    private Station station1;
    private Station station2;

    @BeforeEach
    void setup() {
        line = lineRepository.save(new Line("2호선", "red", 10));
        station1 = stationRepository.save(new Station("방배역"));
        station2 = stationRepository.save(new Station("서초역"));
        line.addStation(station1);
        line.addStation(station2);
    }

    @Test
    @DisplayName("line 생성")
    void createLine() {
        assertThat(line.getId()).isNotNull();
    }

    @Test
    @DisplayName("line 삭제")
    void deleteLine() {
        line.clearRelatedLines();
        lineRepository.delete(line);
        Optional<Line> optionalLine = lineRepository.findById(line.getId());
        assertThat(optionalLine).isNotPresent();
        assertThat(station1.getLine()).isNull();
    }
}
