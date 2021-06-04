package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @DisplayName("노선 생성")
    @Test
    public void 노선생성시_생성확인() throws Exception {
        //when
        Line line = Line.create("신분당선", "red");

        //then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("신분당선");
    }
}
