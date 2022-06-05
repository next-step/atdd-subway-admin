package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 목록에 대한 단위 테스트")
class SectionsTest {

    @DisplayName("구간에 역을 추가하면 정상적으로 추가되어야 한다")
    @Test
    void sections_add_test() {
        // given
        Station 신도림역 = new Station(1L, "신도림");
        Station 대림역 = new Station(2L, "대림");
        Section 구간 = Section.of(신도림역, 대림역, 10L);

        // when
        Sections sections = new Sections();
        sections.add(구간);

        // then
        List<Station> result = sections.getOrderedStations();
        assertThat(result).containsExactly(신도림역, 대림역);
    }

    @DisplayName("기존 구간 하행종점에 구간이 추가되면 추가된 순서에 맞게 조회되어야 한다")
    @Test
    void sections_add_downStream_endPoint_test() {
        // given
        Station 신도림역 = new Station(1L, "신도림");
        Station 대림역 = new Station(2L, "대림");
        Section 구간1 = Section.of(신도림역, 대림역, 10L);

        Station 구로디지털단지역 = new Station(3L, "구로디지털단지");
        Section 구간2 = Section.of(대림역, 구로디지털단지역, 7L);

        // when
        Sections sections = new Sections();
        sections.add(구간1);
        sections.add(구간2);

        // then
        List<Station> result = sections.getOrderedStations();
        assertThat(result).containsExactly(신도림역, 대림역, 구로디지털단지역);
    }

    @DisplayName("기존 구간의 중간에 새로운 구간이 추가되면 순서에 맞게 역이 조회되어야 한다")
    @Test
    void sections_add_between_test() {
        // given
        Station 신도림역 = new Station(1L, "신도림");
        Station 대림역 = new Station(2L, "대림");
        Section 구간1 = Section.of(신도림역, 대림역, 10L);

        Station 신대방역 = new Station(3L, "신대방");
        Section 구간2 = Section.of(대림역, 신대방역, 7L);

        Station 구로디지털단지역 = new Station(4L, "구로디지털단지");
        Section 구간3 = Section.of(대림역, 구로디지털단지역, 5L);

        // when
        Sections sections = new Sections();

        sections.add(구간1);
        sections.add(구간2);
        sections.add(구간3);

        // then
        List<Station> result = sections.getOrderedStations();
        assertThat(result).containsExactly(신도림역, 대림역, 구로디지털단지역, 신대방역);
    }

    @DisplayName("2개의 구간에서 중간 역이 삭제되면 기존의 upStation 이 "
        + "target 의 downStation 으로 변경되고 distance 가 합쳐져야 한다")
    @Test
    void relocate_test() {
        // given
        Station 구간1_upStation = new Station(1L, "테스트");
        Station 구간1_downStation = new Station(2L, "테스트2");
        Section 구간1 = Section.of(구간1_upStation, 구간1_downStation, 10L);

        Station 구간2_upStation = new Station(2L, "테스트2");
        Station 구간2_downStation = new Station(3L, "테스트3");
        Section 구간2 = Section.of(구간2_upStation, 구간2_downStation, 7L);

        // when
        Sections sections = new Sections();
        sections.add(구간1);
        sections.add(구간2);

        // then
        sections.deleteStation(구간1_downStation);

        assertAll(
            () -> assertThat(구간1.getDownStation()).isEqualTo(구간2.getDownStation()),
            () -> assertThat(구간1.getDistanceValue()).isEqualTo(17),
            () -> assertThat(sections.getItems().size()).isEqualTo(1)
        );
    }
}
