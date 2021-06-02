package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @Test
    void mapOrderByUpStationToDownStation() {
        Station 역_1 = new Station("역_1");
        Station 역_2 = new Station("역_2");
        Station 역_3 = new Station("역_3");
        Station 역_4 = new Station("역_4");
        Station 역_5 = new Station("역_5");
        Station 역_6 = new Station("역_6");
        Station 역_7 = new Station("역_7");
        Station 역_8 = new Station("역_8");
        Station 역_9 = new Station("역_9");
        Station 역_10 = new Station("역_10");
        Station 역_11 = new Station("역_11");
        Station 역_12 = new Station("역_12");

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

        Sections sorted = new Sections(sections);
        List<Section> collect = sorted.mapOrderByUpStationToDownStation(item -> item).collect(Collectors.toList());

        assertThat(collect.get(0).getUpStation())
                .isEqualTo(역_1);
        assertThat(collect.get(1).getUpStation())
                .isEqualTo(역_2);
        assertThat(collect.get(2).getUpStation())
                .isEqualTo(역_3);
        assertThat(collect.get(3).getUpStation())
                .isEqualTo(역_4);
        assertThat(collect.get(4).getUpStation())
                .isEqualTo(역_5);
        assertThat(collect.get(5).getUpStation())
                .isEqualTo(역_6);
        assertThat(collect.get(6).getUpStation())
                .isEqualTo(역_7);
        assertThat(collect.get(7).getUpStation())
                .isEqualTo(역_8);
        assertThat(collect.get(8).getUpStation())
                .isEqualTo(역_9);
        assertThat(collect.get(9).getUpStation())
                .isEqualTo(역_10);
        assertThat(collect.get(10).getUpStation())
                .isEqualTo(역_11);
        assertThat(collect.get(10).getDownStation())
                .isEqualTo(역_12);
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

        Sections sorted = new Sections(sections);
        List<Section> collect = sorted.mapOrderByUpStationToDownStation(item -> item).collect(Collectors.toList());

        assertThat(collect.get(0).getUpStation())
                .isEqualTo(역_1);
        assertThat(collect.get(1).getUpStation())
                .isEqualTo(역_2);
        assertThat(collect.get(1).getDownStation())
                .isEqualTo(역_3);
    }
}