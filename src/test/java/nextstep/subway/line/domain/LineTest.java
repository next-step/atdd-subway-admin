package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class LineTest {

	@Test
	@DisplayName("노선에 역 사이 구간 정보를 저장시, 구간 정보에도 노선의 정보가 저장되어야 한다.")
	void addSectionTest() {
		Line line = new Line("5호선", "보라");
		Station up = new Station("목동");
		Station down = new Station("오목교");

		line.addSectionBetween(up, down, 10);

		Sections sections = line.getSections();
		assertThat(sections.isIn(line)).isTrue();
	}

}