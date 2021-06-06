package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @DisplayName("노선 생성")
    @Test
    public void 노선생성시_생성확인() {
        //when
        Line line = Line.create("신분당선", "red");

        //then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선 수정")
    @Test
    public void 노선수정시_수정확인() {
        //given
        Line line = Line.create("신분당선", "red");

        //when
        line.change("구분당선", "blue");

        //then
        assertThat(line.getName()).isEqualTo("구분당선");
        assertThat(line.getColor()).isEqualTo("blue");
    }
}
