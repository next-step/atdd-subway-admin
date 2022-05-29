package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("두 객체가 같은지 검증")
    void verifySameLineObject() {
        assertThat(line).isEqualTo(new Line("신분당선", "bg-red-600"));
    }

    @Test
    @DisplayName("이름과 색상을 바꾸고 동일한지 검증")
    void sameChangedNameAndChangedColor() {
        line.change(new LineRequest("구분당선", "bg-red-100"));

        assertAll(
                () -> assertThat(line.name()).isEqualTo("구분당선"),
                () -> assertThat(line.color()).isEqualTo("bg-red-100")
        );
    }

    @Test
    @DisplayName("구간을 추가시 들어가는지 검증")
    void addSection() {
        Station seoCho = new Station("서초역");
        Station gangNam = new Station("강남역");
        line.addSection(new Section(line, seoCho, gangNam, Distance.of(10L)));

        assertThat(line.sections().size()).isEqualTo(1);
    }
}
