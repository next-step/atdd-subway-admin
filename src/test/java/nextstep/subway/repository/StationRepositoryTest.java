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
class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station station;
    private Line line;

    @BeforeEach
    void setup() {
        station = stationRepository.save(new Station("서초역"));
        line = lineRepository.save(new Line("2호선", "red", 10));
        station.setLine(line);
    }

    @Test
    @DisplayName("station 생성")
    void createStation() {
        assertThat(station.getId()).isNotNull();
    }

    @Test
    @DisplayName("stations 삭제 후 조회")
    void deleteStation() {
        station.clearRelatedStation();
        stationRepository.delete(station);
        Optional<Station> actual = stationRepository.findById(station.getId());
        assertThat(actual).isNotPresent();
    }
}
