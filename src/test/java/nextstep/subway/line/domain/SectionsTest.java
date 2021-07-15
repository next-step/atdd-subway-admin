package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.NotMatchStationException;
import nextstep.subway.common.exception.SectionsRemoveInValidSizeException;
import nextstep.subway.station.domain.Station;

class SectionsTest {

	Sections sections;
	Line line;
	Station 신도림역;
	Station 잠실역;
	Station 서울대입구역;

	@BeforeEach
	void setup() {
		신도림역 = new Station("신도림역");
		잠실역 = new Station("잠실역");
		서울대입구역 = new Station("서울대입구역");

		line = new Line("2호선", "green", 신도림역, 잠실역, 10);
		Section section1 = new Section(line, 신도림역, 서울대입구역, 10);
		Section section2 = new Section(line, 서울대입구역, 잠실역, 4);
		List<Section> sectionList = new ArrayList<>();
		sectionList.add(section1);
		sectionList.add(section2);
		sections = new Sections(sectionList);
	}

	@DisplayName("secions에 포함하는 모든 역들을 중복없이 가져온다")
	@Test
	void 역_찾기() {
		List<Station> stations = sections.getStations();
		assertThat(stations).contains(
			신도림역,
			서울대입구역,
			잠실역
		);
	}

	@DisplayName("하행역으로 section을 찾는다.")
	@Test
	void 하행역으로_구간_찾기() {
		Section section = sections.findSectionByDownStation(서울대입구역).orElse(null);

		assertThat(section).isNotNull();
		assertThat(section.getDownStation()).isEqualTo(서울대입구역);
		assertThat(section.getUpStation()).isEqualTo(신도림역);
	}

	@DisplayName("상행역으로 section을 찾는다.")
	@Test
	void 상행역으로_구간_찾기() {
		Section section = sections.findSectionByUpStation(서울대입구역).orElse(null);

		assertThat(section).isNotNull();
		assertThat(section.getDownStation()).isEqualTo(잠실역);
		assertThat(section.getUpStation()).isEqualTo(서울대입구역);
	}

	@DisplayName("구간을 삭제한다")
	@Test
	void 구간을_삭제한다() {
		sections.removeStation(line, 서울대입구역);

		assertThat(sections.getStations()).doesNotContain(서울대입구역);
	}

	@DisplayName("구간에 존재하지 않는 역은 삭제할 수 없다.")
	@Test
	void 구간에_존재하지_않는_역_삭제() {
		Station 봉천역 = new Station("봉천역");
		assertThrows(NotMatchStationException.class, () -> {
			sections.removeStation(line, 봉천역);
		});
	}

	@DisplayName("구간이 1개인 경우, 삭제할 수 없다.")
	@Test
	void 구간이_한_개_삭제() {
		List<Section> sectionList = new ArrayList<>();
		sectionList.add(new Section(line, 신도림역, 잠실역, 10));
		sections = new Sections(sectionList);

		assertThrows(SectionsRemoveInValidSizeException.class, () -> {
			sections.removeStation(line, 신도림역);
		});
	}

}
