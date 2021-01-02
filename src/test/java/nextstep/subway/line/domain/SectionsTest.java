package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("노선에 여러 구간을 등록하고 항상 정렬 된 목록을 받을 수 있다")
    public void addManySectionAndAlwaysSortedList() {
        // given
        Line line = new Line("2호선", "green");

        Station 강남 = new Station("강남");
        Station 역삼 = new Station("역삼");
        Station 선릉 = new Station("선릉");
        Station 잠실나루 = new Station("잠실나루");
        Station 잠실 = new Station("잠실");

        Section s1 = new Section(line, 잠실, 선릉, 10L);
        Section s2 = new Section(line, 잠실, 잠실나루, 5L);
        Section s3 = new Section(line, 선릉, 역삼, 10L);
        Section s4 = new Section(line, 역삼, 강남, 10L);

        Sections sections = new Sections();

        // when
        sections.add(s1);
        sections.add(s2);
        sections.add(s3);
        sections.add(s4);

        // then
        List<Station> stations = sections.getStations();
        assertThat(stations).hasSize(5);
        assertThat(stations).containsExactly(잠실, 잠실나루, 선릉, 역삼, 강남);
    }

    @Test
    @DisplayName("노선에 역을 제거할 수 있다.")
    public void stationRemoveSuccess() {
        // given
        Line line = new Line("2호선", "green");

        Station 강남 = new Station("강남");
        Station 역삼 = new Station("역삼");
        Station 선릉 = new Station("선릉");
        Station 잠실나루 = new Station("잠실나루");
        Station 잠실 = new Station("잠실");

        line.addSection(잠실, 선릉, 10L);
        line.addSection(잠실, 잠실나루, 5L);
        line.addSection(선릉, 역삼, 10L);
        line.addSection(역삼, 강남, 10L);

        // when
        // [잠실, 잠실나루, 선릉, 역삼, 강남] -> [잠실, 선릉, 역삼] : 중간역(잠실나루)제거, 종점(강남)제거
        line.removeStationInSection(잠실나루);
        line.removeStationInSection(강남);

        //then
        List<Station> stations = line.getStations();
        assertThat(stations).containsExactly(잠실, 선릉, 역삼);
    }
}
