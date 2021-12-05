package nextstep.subway.line;

import static nextstep.subway.line.SectionTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

@DisplayName("호선 도메인 테스트")
public class LineTest {

	public static final Line FIRST = Line.of(1L, "1호선", "RED");
	public static final Line SECOND = Line.of(2L, "2호선", "BLUE");

	@Test
	@DisplayName("생성 테스트")
	void createTest() {
		// given
		Long id = 1L;
		String name = "1호선";

		// when
		Line line = Line.of(id, name, "red");

		// then
		assertThat(line).isEqualTo(Line.of(id, name, "red"));
	}

	@Test
	@DisplayName("상행과 하행과 함께 생성 테스트")
	void createTest2() {
		// given
		String name = "1호선";
		String color = "red";
		List<Section> sections = Collections.singletonList(SECTION_1);

		// when
		Line line = Line.of(1L, name, color, sections);

		// then
		assertThat(line).isEqualTo(Line.of(1L, name, color, sections));
	}

	@Test
	@DisplayName("Line 이 주어지면 업데이트한다")
	void updateTest() {
		// given
		Line line = Line.of(1L, "신분당선", "red");
		Line newLine = Line.of(1L, "1호선", "green");

		// when
		line.update(newLine);

		// then
		assertThat(line.getColor()).isEqualTo(newLine.getColor());
		assertThat(line.getName()).isEqualTo(newLine.getName());
	}

	@Test
	@DisplayName("Section 을 추가한다")
	void addSectionTest() {
		// given
		Line line = Line.of(1L, "신분당선", "red");

		// when
		line.addSection(SECTION_1);

		// then
		assertAll(
			() -> assertThat(line.getSections().contains(SECTION_1)).isTrue(),
			() -> assertThat(line).isEqualTo(SECTION_1.getLine())
		);
	}

	@Test
	@DisplayName("Stations 들을 조회한다")
	void getStationsTest() {
		// given
		Line line = Line.of(1L, "신분당선", "red");
		line.addSection(SECTION_1);

		// when
		List<Station> stations = line.getOrderedStations();

		// then
		assertThat(stations).containsExactly(SECTION_1.getUpStation(), SECTION_1.getDownStation());
	}
}
