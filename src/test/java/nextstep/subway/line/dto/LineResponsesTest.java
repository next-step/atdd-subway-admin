package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LineResponsesTest {
    @Test
    @DisplayName("노선 리스트 생성")
    public void createLinesResponseTest() {
        //given
        Line line1 = new Line("1호선", "blue");
        Line line2 = new Line("2호선", "green");

        //when
        LineResponses linesResponse = LineResponses.of(Arrays.asList(line1, line2));

        //them
        assertThat(linesResponse).isEqualTo(LineResponses.of(Arrays.asList(line1, line2)));
    }
}
