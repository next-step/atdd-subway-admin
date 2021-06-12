package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

    @Test
    @DisplayName("만들어진 역에서 같은역을 하나 더 추가하면 DataIntegrityViolationException가 발생한다.")
    void sectionExceptionTest() {
        // given
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("강남역"));

        // when & then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> stationRepository.saveAll(stations));
    }

    @Test
    @DisplayName("만들어진 역에서 역을 하나 더 추가가 되는지 확인한다.")
    void sectionAddTest() {
        // given
        List<Station> stations = new ArrayList<>(Arrays.asList(new Station("강남역"), new Station("역삼역")));

        // when
        Station 서울대입구역 = new Station("서울대입구역");
        stations.add(서울대입구역);

        // then
        assertThat(stations.size()).isEqualTo(3);
    }
}