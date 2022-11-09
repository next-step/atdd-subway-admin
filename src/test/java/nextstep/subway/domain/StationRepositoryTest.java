package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        Station station = Station.from("신사역");

        Station savedStation = stationRepository.save(station);

        assertAll(
                () -> assertThat(savedStation).isNotNull(),
                () -> assertThat(savedStation.getId()).isEqualTo(station.getId())
        );
    }

    @DisplayName("지하철역 이름 중복 시 지하철역 생성 예외발생")
    @Test
    void duplicatedStationName() {
        Station station = Station.from("신사역");
        stationRepository.save(station);

        Station duplicateStation = Station.from("신사역");

        Assertions.assertThatThrownBy(() -> stationRepository.save(duplicateStation))
                .isInstanceOf(DataIntegrityViolationException.class);

    }
}