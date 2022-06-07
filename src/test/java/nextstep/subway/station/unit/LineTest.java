package nextstep.subway.station.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineTest {
	private Line sinbundangLine;
	private Station gangnamStation;
	private Station gwanggyoStaion;
	private Station jeongjaStation;

	@BeforeEach
	void init() {
		// given
		sinbundangLine = new Line("신분당선", "bg-red-600");
		gangnamStation = new Station("강남역");
		gwanggyoStaion = new Station("광교역");
		jeongjaStation = new Station("정자역");
	}

	@DisplayName("지하철에 구간 생성")
	@Test
	void createSection() {
		// when
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, gwanggyoStaion);
	}

	@DisplayName("구간 사이에 새로운 구간 생성")
	@Test
	void createSectionInMiddle() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(gangnamStation, jeongjaStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, jeongjaStation, gwanggyoStaion);
	}

	@DisplayName("상행역 앞에 새로운 구간 생성")
	@Test
	void createSectionInFrontOfUpStation() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(jeongjaStation, gangnamStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(jeongjaStation, gangnamStation, gwanggyoStaion);
	}

	@DisplayName("하행역 뒤에 새로운 구간 생성")
	@Test
	void createSectionBehindDownStation() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when
		sinbundangLine.addSection(gwanggyoStaion, jeongjaStation, 10);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(gangnamStation, gwanggyoStaion, jeongjaStation);
	}

	@DisplayName("구간이 이미 등록 되어 있는 경우 오류")
	@Test
	void errorIfDuplicate() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when then
		assertThatThrownBy(() -> sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 10))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함 있지 않은 경우 오류")
	@Test
	void errorIfNotIncludeStation() {
		// given
		Station gyodaeStation = new Station("교대역");
		Station jamsilStation = new Station("잠실역");
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when then
		assertThatThrownBy(() -> sinbundangLine.addSection(gyodaeStation, jamsilStation, 10))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을 경우 오류")
	@Test
	void errorIfEqualOrLongerDistance() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);

		// when then
		assertThatThrownBy(() -> sinbundangLine.addSection(gangnamStation, jeongjaStation, 40))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("노선의 맨 앞 상행역 삭제")
	@Test
	void removeFrontUpStation() {
		// given
		sinbundangLine.addSection(gangnamStation, gwanggyoStaion, 30);
		sinbundangLine.addSection(gangnamStation, jeongjaStation, 10);

		// when
		sinbundangLine.removeSection(gangnamStation);

		// then
		assertThat(sinbundangLine.getStation()).containsExactly(jeongjaStation, gwanggyoStaion);
	}
}
