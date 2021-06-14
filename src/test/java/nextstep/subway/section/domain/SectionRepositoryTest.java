package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SectionRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("section 저장시 station 매핑 확인")
    @Test
    void saveWithStation() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        Section section = new Section(upStation, downStation, 1000);

        Section savedSection = sectionRepository.save(section);

        assertThat(savedSection.getId()).isNotNull();
        assertThat(savedSection.getDownStation()).isEqualTo(downStation);
        assertThat(savedSection.getUpStation()).isEqualTo(upStation);
    }

}