package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Station upStation1;
    private Station upStation2;
    private Station downStation1;
    private Station downStation2;
    private Sections sections;

    @BeforeEach
    void setUp() {
        upStation1 = new Station("상행1");
        upStation2 = new Station("상행2");
        downStation1 = new Station("하행1");
        downStation2 = new Station("하행2");
        sections = new Sections(new ArrayList<>(Arrays.asList(
                new Section(upStation1, downStation1, 10),
                new Section(upStation1, downStation2, 10),
                new Section(upStation1, downStation2, 10)
        )));
    }

    @DisplayName("상행역 부터 하행역 순으로 정렬되어야 한다.")
    @Test
    void getSortedStations() {
        //when
        List<Station> actual = sections.getSortedStations();

        //then
        assertAll(() -> {
            assertThat(actual.size()).isEqualTo(3);
            assertThat(actual.get(0)).isEqualTo(upStation1);
            assertThat(actual.get(1)).isEqualTo(downStation1);
            assertThat(actual.get(2)).isEqualTo(downStation2);
        });
    }

    @Test
    void add() {
        //given
        Section section = new Section(upStation2, downStation2, 10);

        //when
        sections.add(section);

        //then
        assertThat(sections.getSections()).contains(section);
    }
}