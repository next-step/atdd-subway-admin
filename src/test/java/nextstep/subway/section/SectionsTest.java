package nextstep.subway.section;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    void sortBy() {
        // given
        Station 역삼 = new Station("역삼");
        Station 선릉 = new Station("선릉");
        Station 강남 = new Station("강남");
        Station 잠실 = new Station("잠실");
        Sections sections = new Sections(Arrays.asList(
                new Section(역삼, 선릉, 5),
                new Section(강남, 역삼, 5),
                new Section(선릉, 잠실, 5)
        ));
        // when
        List<Station> stations = sections.toStations();
        // then
        assertThat(stations).containsExactly(강남, 역삼, 선릉, 잠실);
    }
}