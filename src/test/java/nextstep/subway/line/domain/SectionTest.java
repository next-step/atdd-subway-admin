package nextstep.subway.line.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @Test
    void create() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("삼성역");

        //when
        Section actual = new Section(upStation, downStation, 10);

        //then
        assertAll(() -> {
            assertThat(actual.getUpStation()).isSameAs(upStation);
            assertThat(actual.getDownStation()).isSameAs(downStation);
        });
    }

    @Test
    void toLine() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("삼성역");
        Section section = new Section(upStation, downStation, 10);
        Line line = new Line("2호선", "green");

        //when
        section.toLine(line);

        //then
        assertAll(() -> {
            assertThat(section.getLine().getName()).isEqualTo("2호선");
            assertThat(section.getLine().getColor()).isEqualTo("green");
        });
    }
}