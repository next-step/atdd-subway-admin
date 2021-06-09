package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @Test
    @DisplayName("상행, 하행정보를 가져온다.")
    void stationTest() {
        Station gangnam = stationRepository.save(new Station("강남역"));
        Station yeoksam = stationRepository.save(new Station("역삼역"));

        assertAll(
                () -> assertThat(gangnam.getName()).isEqualTo("강남역"),
                () -> assertThat(yeoksam.getName()).isEqualTo("역삼역")
        );
    }

    @Test
    @DisplayName("상행 조회, 하행 조회")
    void findStation() {
        // given
        Station gangnam = stationRepository.save(new Station("강남역"));

        // when
        Station findStation = stationRepository.findById(gangnam.getId()).get();

        // then
        assertThat(findStation.getId()).isEqualTo(gangnam.getId());
    }
}