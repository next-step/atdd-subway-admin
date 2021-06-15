package nextstep.subway.section.domain;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.SubwayLogicException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

class SectionsTest {

	private Line line;
	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Station 종합운동장역;
	private Station 잠실역;

	@BeforeEach
	void setup() {
		this.line = new Line("2호선", "green");
		this.강남역 = new Station("강남");
		this.역삼역 = new Station("역삼");
		this.선릉역 = new Station("선릉");
		this.종합운동장역 = new Station("종합운동장");
		this.잠실역 = new Station("잠실");
	}

	@Test
	@DisplayName("Section 목록으로 부터 Station을 상행에서 하행순으로 정렬하는지 테스트")
	void orderedSections() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.역삼역, 1);
		Section section2 = new Section(line, this.역삼역, this.선릉역, 1);
		Section section3 = new Section(line, this.선릉역, this.종합운동장역, 1);
		Section section4 = new Section(line, this.종합운동장역, this.잠실역, 1);

		sections.addSection(section2);
		sections.addSection(section1);
		sections.addSection(section3);
		sections.addSection(section4);

		List<Station> orderedStations = sections.getOrderedStations();
		Assertions.assertThat(orderedStations).containsExactly(강남역, 역삼역, 선릉역, 종합운동장역, 잠실역);
	}

	@Test
	@DisplayName("HappyPath - 구간 추가 기능 테스트")
	void addSection() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.잠실역, 20);
		Section section2 = new Section(line, this.강남역, this.선릉역, 15);
		Section section3 = new Section(line, this.선릉역, this.종합운동장역, 3);
		Section section4 = new Section(line, this.역삼역, this.선릉역, 5);

		sections.addSection(section1);
		sections.addSection(section2);
		sections.addSection(section3);
		sections.addSection(section4);

		List<Station> orderedStations = sections.getOrderedStations();
		Assertions.assertThat(orderedStations).containsExactly(강남역, 역삼역, 선릉역, 종합운동장역, 잠실역);
	}

	@Test
	@DisplayName("구간 추가기능 테스트 - 이미 존재하는 구간 추가시 오류발생")
	void test_alreadyExistSection() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.잠실역, 20);

		sections.addSection(section1);

		Assertions.assertThatThrownBy(() -> {
			sections.addSection(section1);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("해당구간은 이미 구성되어있어 추가할 수 없습니다.");
	}

	@Test
	@DisplayName("구간 추가기능 테스트 - 연결불가능한 새로운 구간을 추가시 오류발생")
	void test_notContainedSection() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.역삼역, 20);
		Section section2 = new Section(line, this.종합운동장역, this.잠실역, 20);

		sections.addSection(section1);

		Assertions.assertThatThrownBy(() -> {
			sections.addSection(section2);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("해당 구간은 연결 가능한 상행, 하행역이 없습니다.");
	}

	@Test
	@DisplayName("구간 추가기능 테스트 - 추가 구간의 거리가 기존의 거리보다 길거나 같으면 오류발생")
	void test_distanceLonger() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.선릉역, 20);
		Section section2 = new Section(line, this.강남역, this.역삼역, 100);

		sections.addSection(section1);

		Assertions.assertThatThrownBy(() -> {
			sections.addSection(section2);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("기존 구간의 길이가 추가하려는 구간보다 짧습니다.");
	}

	@Test
	@DisplayName("구간 제거 테스트 - 하나의 구간만 존재하고 그 구간을 제거시 오류")
	void test_유일한구간제거() {
		Sections sections = new Sections();
		Section section = new Section(line, this.강남역, this.선릉역, 20);
		sections.addSection(section);

		Assertions.assertThatThrownBy(() -> {
			sections.removeSection(this.강남역);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("노선의 유일한 구간으로 제거할 수 없습니다.");
	}

	@Test
	@DisplayName("구간 제거 테스트 - 구간내에 존재하지 않는 역으로 제거시 오류")
	void test_없는역으로구간제거() {
		Sections sections = new Sections();
		Section section1 = new Section(line, this.강남역, this.선릉역, 20);
		Section section2 = new Section(line, this.선릉역, this.종합운동장역, 10);

		sections.addSection(section1);
		sections.addSection(section2);

		Assertions.assertThatThrownBy(() -> {
			sections.removeSection(this.잠실역);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("제거 가능한 구간이 없습니다");
	}

}