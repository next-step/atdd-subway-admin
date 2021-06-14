package nextstep.subway.linestation.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DataJpaTest
public class LineStationRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LineStationRepository lineStationRepository;

    @DisplayName("LineStation 저장 테스트")
    @Test
    void given_LineStation_when_SaveLineStation_then_ReturnPersistedLineStation() {
        // given
        final Line line = testEntityManager.persist(new Line("1호선", "bg-blue-600"));
        final Station station = testEntityManager.persist(new Station("강남역"));
        final Station previousStation = testEntityManager.persist(new Station("역삼역"));
        final Integer previousDistance = 100;
        final Station nextStation = testEntityManager.persist(new Station("신도림"));
        final Integer nextDistance = 200;
        final LineStation lineStation = new LineStation(line, station);
        lineStation.previous(new LineStation(line, previousStation), previousDistance);
        lineStation.next(new LineStation(line, nextStation), nextDistance);

        // when
        final LineStation actual = lineStationRepository.save(lineStation);

        // then
        assertAll(
            () -> assertThat(actual.getStation()).isEqualTo(station),
            () -> assertThat(actual.getPreviousStation()).isEqualTo(new LineStation(line, previousStation)),
            () -> assertThat(actual.getPreviousDistance()).isEqualTo(previousDistance),
            () -> assertThat(actual.getNextStation()).isEqualTo(new LineStation(line, nextStation)),
            () -> assertThat(actual.getNextDistance()).isEqualTo(nextDistance)
        );
    }
}
