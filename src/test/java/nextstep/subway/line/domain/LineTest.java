package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
	static Line 신분당선 = new Line("신분당선", "빨강");

	@Test
	@DisplayName("노선에 역 사이 구간 정보를 저장시, 구간 정보에도 노선의 정보가 저장되어야 한다.")
	void addSectionTest() {
		Line line = new Line("신분당선", "빨강");

		line.addSection(강남, 양재, 10);

		Sections sections = line.getSections();
		assertThat(sections.isIn(line)).isTrue();
	}
}
