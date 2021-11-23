package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;

public class LineTest {

	@Test
	@DisplayName("Line 이 주어지면 업데이트한다")
	void updateTest() {
		// given
		Line line = new Line("신분당선", "red");
		Line newLine = new Line("1호선", "green");

		// when
		line.update(newLine);

		// then
		assertThat(line.getColor()).isEqualTo(newLine.getColor());
		assertThat(line.getName()).isEqualTo(newLine.getName());

	}
}
