package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.exception.SubwayLogicException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

class SectionTest {
	private Line line;
	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Station 종합운동장역;
	private Station 잠실역;
	private Section section;

	@BeforeEach
	void setup() {
		this.line = new Line("2호선", "green");
		this.강남역 = new Station("강남");
		this.역삼역 = new Station("역삼");
		this.선릉역 = new Station("선릉");
		this.종합운동장역 = new Station("종합운동장");
		this.잠실역 = new Station("잠실");
		this.section = new Section(line, this.잠실역, this.강남역, 20);
	}

	@Test
	@DisplayName("추가 불가능한 경우 오류 발생 테스트")
	void testValidateSectionDistance() {
		Section newSection = new Section(line, this.잠실역, this.종합운동장역, 40);
		assertThatThrownBy(() -> {
			section.validateSectionDistance(newSection);
		}).isInstanceOf(SubwayLogicException.class)
			.hasMessageContaining("기존 구간의 길이가 추가하려는 구간보다 짧습니다.");
	}

	@Test
	@DisplayName("새로운 구간 추가시 상행,하행역을 재구성할 수 있는지 확인")
	void test_역_재구성여부_테스트() {
		Section newSection1 = new Section(line, this.잠실역, this.종합운동장역, 3);
		Section newSection2 = new Section(line, this.선릉역, this.강남역, 3);
		Section newSection3 = new Section(line, this.선릉역, this.역삼역, 3);

		assertThat(section.isBuildable(newSection1)).isTrue();
		assertThat(section.isBuildable(newSection2)).isTrue();
		assertThat(section.isBuildable(newSection3)).isFalse();
	}

	@Test
	@DisplayName("새로운 구간 추가시 기존 구간을 정상적으로 변경하는지 확인")
	void test_구간변경확인() {
		Section newSection = new Section(line, this.잠실역, this.종합운동장역, 3);
		this.section.rebuildStation(newSection);

		assertThat(this.section.getDistance()).isEqualTo(new Distance(17));
		assertThat(this.section.getUpStation()).isEqualTo(this.종합운동장역);
	}
}