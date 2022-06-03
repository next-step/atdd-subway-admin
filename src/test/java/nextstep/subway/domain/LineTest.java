package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private Line line;
    private Station seoCho, gangNam, yangJae;

    @BeforeEach
    void setUp() {
        line = new Line("신분당선", "bg-red-600");
        seoCho = new Station("서초역");
        gangNam = new Station("강남역");
        yangJae = new Station("양재역");
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
        line.addSection(new Section(line, seoCho, gangNam, Distance.of(10L)));

        assertThat(line.sections().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간을 삭제시 삭제되었는지 검증")
    void deleteSection() {
        line.addSection(new Section(line, seoCho, gangNam, Distance.of(10L)));
        line.addSection(new Section(line, gangNam, yangJae, Distance.of(5L)));
        line.deleteSection(yangJae);

        assertThat(line.sections().size()).isEqualTo(1);
    }
}
