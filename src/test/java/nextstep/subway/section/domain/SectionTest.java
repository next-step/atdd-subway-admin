package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class SectionTest {

    @Autowired
    StationRepository stationRepository;
    @Autowired
    SectionRepository sectionRepository;

    Station 강남역;
    Station 삼성역;
    Station 잠실역;

    @BeforeEach
    public void init() {
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
    }

    @DisplayName("Section Entity를 저장후 조회값과 비교")
    @Test
    void create() {
        int distance = 200;
        Section section = Section.create(강남역, 잠실역, distance);

        Section savedSection = sectionRepository.save(section);
        Section findSection = sectionRepository.getOne(savedSection.getId());

        assertThat(findSection).isEqualTo(savedSection);
        assertThat(findSection.getUpStation()).isEqualTo(강남역);
        assertThat(findSection.getDownStation()).isEqualTo(잠실역);
    }

    @DisplayName("상행선역과 동일한지 비교")
    @Test
    void equalsUpStation() {
        Section section = Section.create(강남역, 잠실역, 200);
        Section newSection = Section.create(강남역, 삼성역, 100);

        assertTrue(section.equalsUpStation(newSection));
    }

    @DisplayName("상행선역과 동일한지 비교")
    @Test
    void equalsDownStation() {
        Section section = Section.create(강남역, 잠실역, 200);
        Section newSection = Section.create(삼성역, 잠실역, 100);

        assertTrue(section.equalsDownStation(newSection));
    }

    @DisplayName("상행선역과 하행선 사이 새로운역 추가됨 1")
    @Test
    void changeUpSection() {
        int initDistance = 200;
        int newDistance = 50;
        Section section = Section.create(강남역, 잠실역, initDistance);
        Section newSection = Section.create(강남역, 삼성역, newDistance);

        section.changeUpSection(newSection);

        assertThat(section.getUpStation()).isEqualTo(newSection.getDownStation());
    }

    @DisplayName("상행선역과 하행선 사이 새로운역 추가됨 2")
    @Test
    void changeDownSection() {
        int initDistance = 200;
        int newDistance = 150;
        Section section = Section.create(강남역, 잠실역, initDistance);
        Section newSection = Section.create(삼성역, 잠실역, newDistance);

        section.changeDownSection(newSection);

        assertThat(section.getDownStation()).isEqualTo(newSection.getUpStation());
    }

    @DisplayName("동일한거리의 구간은 추가 불가능함")
    @Test
    void unableAddSameDistanceSection() {
        int initDistance = 200;
        int newDistance = 200;
        Section section = Section.create(강남역, 잠실역, initDistance);
        Section newSection = Section.create(삼성역, 잠실역, newDistance);

        assertThatThrownBy(
                () -> section.changeUpSection(newSection)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}