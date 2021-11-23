package nextstep.subway.station.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("역 도메인 관련 기능")
@DataJpaTest
class StationTest {
    @Autowired
    private StationRepository stationRepository;

    private Station station;

    @BeforeEach
    void setUp() {
        station = stationRepository.save(new Station("강남역"));
    }

    @DisplayName("역을 생성한다.")
    @Test
    void createStation() {
        // then
        assertAll(
                () -> assertThat(station.getId()).isNotNull(),
                () -> assertThat(station.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("역을 제거한다.")
    @Test
    void deleteStation() {
        // when
        stationRepository.deleteById(station.getId());

        // then
        assertThat(stationRepository.findAll().size()).isZero();
    }

    @AfterEach
    void tearDown() {
        stationRepository.flush();
    }
}