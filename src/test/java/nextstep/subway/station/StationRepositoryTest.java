package nextstep.subway.station;

import nextstep.subway.Exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DirtiesContext
public class StationRepositoryTest {
    @Autowired
    StationRepository stationRepository;

    @DisplayName("지하철역 저장")
    @Test
    void 저장() {
        //when
        Station station = stationRepository.save(new Station("강남역"));
        //then
        assertThat(station).isNotNull();
        assertThat(station.getName()).isEqualTo("강남역");
    }

    @DisplayName("지하철역 조회")
    @Test
    void 조회() {
        //given
        Station station = stationRepository.save(new Station("강남역"));

        //when
        Station result = stationRepository.findById(station.getId()).orElseThrow(() -> new NotFoundException("데이터 없음" + station.getId()));

        //then
        assertThat(result).isEqualTo(station);
    }

    @DisplayName("지하철역 삭제")
    @Test
    void 삭제() {
        //given
        Station station = stationRepository.save(new Station("강남역"));

        //when
        stationRepository.delete(station);

        //then
        assertThatThrownBy(() -> {
                stationRepository.findById(station.getId()).orElseThrow(() -> new NotFoundException("데이터 없음" + station.getId()));
        }).isInstanceOf(NotFoundException.class).hasMessage("데이터 없음"+ station.getId());
    }
}
