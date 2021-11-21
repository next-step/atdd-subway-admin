package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class StationRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @Test
    @DisplayName("Station Save() 후 id not null 체크")
    void save() {
        // given
        // when
        Station persistStation = stationRepository.save(StationTest.STATION1);

        // then
        assertNotNull(persistStation.getId());
    }

    @Test
    @DisplayName("같은 인스턴스 변수 값의 Station1,Station2 가 주어질때, 영속객체와 일치,불일치 검증")
    void equals() {
        // given
        Station station1 = new Station(StationTest.STATION_NAME1);
        Station station2 = new Station(StationTest.STATION_NAME1);

        // when
        Station persistStation = stationRepository.save(station1);

        // then
        assertAll(
            () -> assertEquals(persistStation, station1),
            () -> assertNotEquals(persistStation, station2)
        );
    }

    @Test
    @DisplayName("Station 2개 저장 후, findAll 조회시 size 2개 검증")
    void findAll() {
        // given
        stationRepository.save(new Station("A역"));
        stationRepository.save(new Station("B역"));

        // when
        List<Station> actual = stationRepository.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("Station 2개 저장, 1개삭제 후 findAll 조회시 size 1개 검증")
    void delete_after_findAll() {
        // given
        stationRepository.save(new Station("A역"));
        Station deleteLine = stationRepository.save(new Station("B역"));

        // when
        deleteLine.delete();
        List<Station> actual = stationRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }


    @Test
    @DisplayName("soft delete Flush 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Station persistStation = stationRepository.save(StationTest.STATION2);
        persistStation.delete();

        // when
        Station actual = stationRepository.findById(persistStation.getId()).get();

        // then
        assertThat(actual.isDeleted()).isTrue();
    }
}
