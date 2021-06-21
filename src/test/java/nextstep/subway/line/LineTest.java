package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.InvalidDistanceException;
import nextstep.subway.station.domain.Station;

public class LineTest {

	private Line lineShinBunDang;
	Station gangNam;
	Station panGyo;
	Station gwangGyo;

	@BeforeEach
	void setUp() {
		lineShinBunDang = new Line("신분당선", "red");
		gangNam = new Station("강남역");
		panGyo = new Station("판교역");
		Section firstSection = new Section(lineShinBunDang, gangNam, panGyo, new Distance(5));
		lineShinBunDang.addSection(firstSection);
		gwangGyo = new Station("광교역");
		Section secondSection = new Section(lineShinBunDang, panGyo, gwangGyo, new Distance(7));
		lineShinBunDang.addSection(secondSection);
	}

	@DisplayName("노선 뒤에 구간 추가하여 목록 조회")
	@Test
	void 노선_뒤에_구간_추가하여_목록_조회() {
		//given
		Station seoul = new Station("서울역");
		Section section = new Section(lineShinBunDang, gwangGyo, seoul, new Distance(7));
		lineShinBunDang.addSection(section);

		//when
		List<Station> actual = lineShinBunDang.getStations();

		//then
		assertThat(actual).isEqualTo(Lists.list(gangNam, panGyo, gwangGyo, seoul));
	}

	@Test
	void 노선_앞에_구간_추가하여_목록_조회() {
		//given
		Station sungSu = new Station("성수역");
		Section section = new Section(lineShinBunDang, sungSu, gangNam, new Distance(3));
		lineShinBunDang.addSection(section);

		//when
		List<Station> actual = lineShinBunDang.getStations();
		List<String> actualNames = actual.stream().map(Station::getName).collect(Collectors.toList());

		//then
		assertThat(actualNames).isEqualTo(Lists.list(sungSu.getName(), gangNam.getName(), panGyo.getName(), gwangGyo.getName()));

	}

	@Test
	void 노선_중간에_구간_추가하여_목록_조회_앞을_기준() {
		//given
		Station saDang = new Station("사당역");
		Section section = new Section(lineShinBunDang, gangNam, saDang, new Distance(2));
		lineShinBunDang.addSection(section);
		//when
		List<Station> actual = lineShinBunDang.getStations();
		List<String> actualNames = actual.stream().map(Station::getName).collect(Collectors.toList());

		//then
		assertThat(actualNames).isEqualTo(Lists.list(gangNam.getName(), saDang.getName(), panGyo.getName(), gwangGyo.getName()));
	}

	@Test
	void 노선_중간에_구간_추가하여_목록_조회_뒤를_기준() {
		//given
		Station saDang = new Station("사당역");
		Section section = new Section(lineShinBunDang, saDang, panGyo, new Distance(2));
		lineShinBunDang.addSection(section);
		//when
		List<Station> actual = lineShinBunDang.getStations();
		List<String> actualNames = actual.stream().map(Station::getName).collect(Collectors.toList());
		//then
		assertThat(actualNames).isEqualTo(Lists.list(gangNam.getName(), saDang.getName(), panGyo.getName(), gwangGyo.getName()));
	}

	@Test
	void 노선_중간에_구간_추가하여_목록_조회_거리가_크거나_같은_경우_에러() {
		//given
		Station saDang = new Station("사당역");
		Section section = new Section(lineShinBunDang, gangNam, saDang, new Distance(10));

		//when
		//then
		assertThatThrownBy(
			() -> lineShinBunDang.addSection(section)
		).isInstanceOf(InvalidDistanceException.class);
	}
}
