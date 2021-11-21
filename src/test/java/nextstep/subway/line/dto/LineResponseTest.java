package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineResponseTest {
    @Test
    void testOf() {
        // givne - Line이 주어졌을때
        Line line = new Line("1", "2");

        // when - of를 호출하면
        LineResponse lineResponse = LineResponse.of(line);

        // then - LineResponse로 변환한다
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor())
        );
    }

    @Test
    void testListOf() {
        // given - List<Line>이 주어졌을때
        List<Line> lines = Arrays.asList(
                new Line("1", "2"),
                new Line("2", "3"),
                new Line("4", "5")
        );

        // when - listOf를 호출하면
        List<LineResponse> lineResponses = LineResponse.listOf(lines);

        // then - LineResponse로 변환한 List<LineResponse>를 반환한다
        assertThat(lineResponses).hasSize(lines.size())
                .contains(LineResponse.of(lines.get(0)), LineResponse.of(lines.get(1)), LineResponse.of(lines.get(2)));
    }
}
