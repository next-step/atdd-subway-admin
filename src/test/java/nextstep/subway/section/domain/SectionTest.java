package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SectionTest {

    @Autowired
    StationRepository stationRepository;
    @Autowired
    SectionRepository sectionRepository;

    Station 상행선;
    Station 하행선;

    @BeforeEach
    public void init() {
        상행선 = new Station("강남역");
        하행선 = new Station("잠실역");
    }

    @DisplayName("Section Entity를 저장후 조회값과 비교")
    @Test
    void create() {
        int distance = 200;
        Section section = Section.create(상행선, 하행선, distance);

        Section savedSection = sectionRepository.save(section);
        Section findSection = sectionRepository.getOne(savedSection.getId());

        assertThat(findSection).isEqualTo(savedSection);
        assertThat(findSection.getUpStation()).isEqualTo(상행선);
        assertThat(findSection.getDownStation()).isEqualTo(하행선);
    }


}