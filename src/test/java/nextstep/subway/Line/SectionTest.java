package nextstep.subway.Line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class SectionTest {

    @Test
    void save_section() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("서초역");
        Section section = new Section(upStation, downStation, 10);
        Line line = new Line("2호선", "green");

        //when
        section.belongLine(line);

        //then
        assertAll(() -> {
            assertThat(section.getLine().getName()).isEqualTo("2호선");
            assertThat(section.getLine().getColor()).isEqualTo("green");
        });
    }
}