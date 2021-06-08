package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Test
    @DisplayName("만들어진 역에서 역을 하나 더 추가하면 UnsupportedOperationException가 발생한다.")
    void sectionTest() {
        // given
        Line line = new Line("2호선", "color-green");
        Station upStation = stationRepository.save(new Station("강남역")); // 상행
        Station downStation = stationRepository.save(new Station("역삼역")); // 하행
        Section section = sectionRepository.save(new Section(line, upStation, downStation, 5L));

        // when
        Station station = new Station("서울대입구역");
        List<Station> stations = section.getStations();

        // then
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> stations.add(station));
    }
}