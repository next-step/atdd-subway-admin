package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Section 관련 테스트")
public class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private StationRepository stationRepository;

    @Test
    void create() {
        Station upStation = stationRepository.save(Station.of("강남역"));
        Station downStation = stationRepository.save(Station.of("역삼역"));
        int distance = 5;

        Section section = sectionRepository.save(Section.of(upStation, downStation, distance));

        assertThat(section.upStationName()).isEqualTo(upStation.getName());
        assertThat(section.downStationName()).isEqualTo(downStation.getName());
        assertThat(section.distance()).isEqualTo(distance);
    }
}
