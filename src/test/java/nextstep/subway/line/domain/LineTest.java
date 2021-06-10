package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @Test
    void create() {
        //given
        String expectedName = "1호선";
        String expectedColor = "blue";

        //when
        Line actual = new Line(expectedName, expectedColor);

        //then
        assertAll(() -> {
            assertThat(actual.getName()).isEqualTo(expectedName);
            assertThat(actual.getColor()).isEqualTo(expectedColor);
        });
    }

    @Test
    void createWithSection() {
        //given
        String expectedName = "1호선";
        String expectedColor = "blue";
        Station upStation1 = new Station("상행1");
        Station downStation1 = new Station("하행1");

        //when
        Line actual = new Line(expectedName, expectedColor, new Section(upStation1, downStation1, 10));

        //then
        assertAll(() ->{
            assertThat(actual.getName()).isEqualTo(expectedName);
            assertThat(actual.getColor()).isEqualTo(expectedColor);
            assertThat(actual.getStations()).contains(upStation1, downStation1);
        });
    }

    @Test
    void update() {
        //given
        Line line = new Line("1호선", "blue");
        Line newLine = new Line("2호선", "green");

        //when
        line.update(newLine);

        //then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo(newLine.getName());
            assertThat(line.getColor()).isEqualTo(newLine.getColor());
        });
    }

    @Test
    void isDifferentNameTrue() {
        //given
        Line line = new Line("1호선", "blue");

        //when
        boolean actual = line.isDifferentName("2호선");

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void isDifferentNameFalse() {
        //given
        Line line = new Line("1호선", "blue");

        //when
        boolean actual = line.isDifferentName("1호선");

        //then
        assertThat(actual).isFalse();
    }
}