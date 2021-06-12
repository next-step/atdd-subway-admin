package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

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
}
