package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    void mapOrderByUpStationToDownStation() {
        Station 역_1 = new Station(1L, "역_1");
        Station 역_2 = new Station(2L, "역_2");
        Station 역_3 = new Station(3L, "역_3");
        Station 역_4 = new Station(4L, "역_4");
        Station 역_5 = new Station(5L, "역_5");
        Station 역_6 = new Station(6L, "역_6");
        Station 역_7 = new Station(7L, "역_7");
        Station 역_8 = new Station(8L, "역_8");
        Station 역_9 = new Station(9L, "역_9");
        Station 역_10 = new Station(10L, "역_10");
        Station 역_11 = new Station(11L, "역_11");
        Station 역_12 = new Station(12L, "역_12");

        // 1 <-> 12번까지 정렬이 되어야함.

        Distance zero = new Distance(0L);

        List<Section> sections = Arrays.asList(
                new Section(new Line("", ""), 역_2, 역_3, zero),
                new Section(new Line("", ""), 역_1, 역_2, zero),
                new Section(new Line("", ""), 역_3, 역_4, zero),
                new Section(new Line("", ""), 역_10, 역_11, zero),
                new Section(new Line("", ""), 역_11, 역_12, zero),
                new Section(new Line("", ""), 역_6, 역_7, zero),
                new Section(new Line("", ""), 역_8, 역_9, zero),
                new Section(new Line("", ""), 역_7, 역_8, zero),
                new Section(new Line("", ""), 역_9, 역_10, zero),
                new Section(new Line("", ""), 역_4, 역_5, zero),
                new Section(new Line("", ""), 역_5, 역_6, zero)
        );

        SortedSections sortedSections = new SortedSections(sections);
        List<SectionResponse> collect = sortedSections
                .toResponse();

        assertThat(collect.get(0).getUpStationId())
                .isEqualTo(역_1.getId());
        assertThat(collect.get(1).getUpStationId())
                .isEqualTo(역_2.getId());
        assertThat(collect.get(2).getUpStationId())
                .isEqualTo(역_3.getId());
        assertThat(collect.get(3).getUpStationId())
                .isEqualTo(역_4.getId());
        assertThat(collect.get(4).getUpStationId())
                .isEqualTo(역_5.getId());
        assertThat(collect.get(5).getUpStationId())
                .isEqualTo(역_6.getId());
        assertThat(collect.get(6).getUpStationId())
                .isEqualTo(역_7.getId());
        assertThat(collect.get(7).getUpStationId())
                .isEqualTo(역_8.getId());
        assertThat(collect.get(8).getUpStationId())
                .isEqualTo(역_9.getId());
        assertThat(collect.get(9).getUpStationId())
                .isEqualTo(역_10.getId());
        assertThat(collect.get(10).getUpStationId())
                .isEqualTo(역_11.getId());
        assertThat(collect.get(10).getDownStationId())
                .isEqualTo(역_12.getId());
    }



    @Test
    void 이미_정렬된_상태() {
        Station 역_1 = new Station("역_1");
        Station 역_2 = new Station("역_2");
        Station 역_3 = new Station("역_3");

        Distance zero = new Distance(0L);

        List<Section> sections = Arrays.asList(
                new Section(new Line("", ""), 역_1, 역_2, zero),
                new Section(new Line("", ""), 역_2, 역_3, zero)
        );

        SortedSections sortedSections = new SortedSections(sections);
        List<SectionResponse> collect = sortedSections.toResponse();

        assertThat(collect.get(0).getUpStationId())
                .isEqualTo(역_1.getId());
        assertThat(collect.get(1).getUpStationId())
                .isEqualTo(역_2.getId());
        assertThat(collect.get(1).getDownStationId())
                .isEqualTo(역_3.getId());
    }
}