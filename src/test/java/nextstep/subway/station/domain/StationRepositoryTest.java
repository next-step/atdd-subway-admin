package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("역을 등록한다.")
    @Test
    void save_역등록() {
        final Station actual = 역_등록("잠실역");

        final Station expected = stationRepository.save(actual);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("등록한 역을 조회한다.")
    @Test
    void findById_노선조회() {
        final Station actual = 역_등록_저장("잠실역");

        final Station expected = 역_조회(actual);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("등록한 노선들을 조회한다.")
    @Test
    void findAll_노선들조회() {
        final Station actual1 = 역_등록_저장("잠실역");
        final Station actual2 = 역_등록_저장("부평구청역");

        final List<Station> lineList = 역들_조회();

        assertAll(
                () -> assertThat(lineList).hasSize(2),
                () -> assertThat(lineList.get(0).getName()).isEqualTo(actual1.getName()),
                () -> assertThat(lineList.get(1).getName()).isEqualTo(actual2.getName())
        );
    }

    @DisplayName("등록한 노선을 삭제한 후 조회 시 예외가 발생한다.")
    @Test
    void delete_예외_노선삭제() {
        final Station actual = 역_등록_저장("잠실역");

        역_삭제(actual);

        assertThatThrownBy(() -> 역_조회(actual))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    private Station 역_등록(String name) {
        return new Station(name);
    }

    private Station 역_등록_저장(String name) {
        return stationRepository.save(new Station(name));
    }

    private Station 역_조회(Station station) {
        return stationRepository.findById(station.getId()).get();
    }

    private List<Station> 역들_조회() {
        return stationRepository.findAll();
    }

    private void 역_삭제(Station actual) {
        stationRepository.delete(actual);
    }
}
