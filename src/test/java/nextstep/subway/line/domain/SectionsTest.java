package nextstep.subway.line.domain;

import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {

    @Test
    void 지하철_상행_하행_에따른_노선_정렬_확인() {
        Line line = new Line("1호선", "red");
        Section section1 = new Section(new Station(2L, "구파발"), new Station(5L, "신촌역"), SectionType.DOWN, new Distance(10));
        Section lastSection = new Section(new Station(5L, "신촌역"), new Station(55L,"대화역"), SectionType.DOWN, new Distance(10));
        Section section4 = new Section(new Station(3L, "서울역"), new Station(2L, "구파발"), SectionType.DOWN, new Distance(5));
        Section section2 = new Section(new Station(1L, "강남역"), new Station(3L, "서울역"), SectionType.UP, new Distance(10));
        Section section3 = new Section(new Station(55L, "대화역"), null, SectionType.DOWN, new Distance(10));
        line.addSection(asList(section4, section1,section3,section2,lastSection));

        List<Section> sections = line.getSections();

        List<Long> collect = sections.stream()
                                     .map(section -> section.getStation().getId())
                                     .collect(Collectors.toList());

        Assertions.assertThat(collect).containsExactly(1L,3L,2L,5L, 55L);
    }

}