package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("지하철 역이 없는 경우 예외 발생")
    void validateStation() {
        // given // when // then
        assertThrows(DataIntegrityViolationException.class,
                () -> stationRepository.save(Station.from(null)));
    }
}
