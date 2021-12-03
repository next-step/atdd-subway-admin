package nextstep.subway.line;

import static nextstep.subway.section.SectionTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;

@DisplayName("호선 도메인 테스트")
public class LineTest {

	public static final Line FIRST = Line.of("1호선", "RED");
	public static final Line SECOND = Line.of("2호선", "BLUE");

	@Test
	@DisplayName("생성 테스트")
	void createTest() {
		// given
		String name = "1호선";

		// when
		Line line = Line.of(name, "red");

		// then
		assertThat(line).isEqualTo(Line.of(name, "red"));
	}

	@Test
	@DisplayName("상행과 하행과 함께 생성 테스트")
	void createTest2() {
		// given
		String name = "1호선";
		String color = "red";
		List<Section> sections = Collections.singletonList(SECTION_1);

		// when
		Line line = Line.of(name, color, sections);

		// then
		assertThat(line).isEqualTo(Line.of(name, color, sections));
	}

	@Test
	@DisplayName("Line 이 주어지면 업데이트한다")
	void updateTest() {
		// given
		Line line = Line.of("신분당선", "red");
		Line newLine = Line.of("1호선", "green");

		// when
		line.update(newLine);

		// then
		assertThat(line.getColor()).isEqualTo(newLine.getColor());
		assertThat(line.getName()).isEqualTo(newLine.getName());
	}
}
