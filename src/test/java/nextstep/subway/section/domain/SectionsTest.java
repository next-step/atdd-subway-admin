package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    private Sections sections = new Sections();

    @Test
    @DisplayName("이어지는 구간을 등록시 역을 조회했을때, 중간역은 중복없이 조회한다.")
    public void lineup() {
        //given
        Station station1 = new Station(1L, "역삼역");
        Station station2 = new Station(2L,"강남역");
        Station station3 = new Station(3L,"역삼역");

        Section section1 = new Section();
        section1.registerStation(station1, station2, 10);

        Section section2 = new Section();
        section2.registerStation(station2, station3, 10);

        sections.add(section1);
        sections.add(section2);

        Set<Station> actual = sections.lineUp();

        assertAll(
            () -> assertThat(actual.size()).isEqualTo(3),
            () -> assertThat(actual).contains(station1, station2, station3)
        );
    }

}