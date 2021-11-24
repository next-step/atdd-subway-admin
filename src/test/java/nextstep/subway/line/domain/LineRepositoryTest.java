package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 노선 DataJpa")
@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("종점역(상행, 하행)이 없는 경우 실패한다.")
    void validateStation() {
        // given
        Line line = Line.of("신분당선", "bg-red-600", null, null, 10);

        // when // then
        assertThrows(DataIntegrityViolationException.class,
                () -> lineRepository.save(line));
    }

    @Test
    @DisplayName("종점역(상행, 하행)이 같은 경우 실패한다.")
    void validateSameStation() {
        // given
        Station station = Station.from("강남");
        Line line = Line.of("신분당선", "bg-red-600", station, station, 10);

        // when // then
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> lineRepository.save(line));
    }
}
