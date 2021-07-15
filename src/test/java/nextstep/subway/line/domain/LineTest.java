package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.DuplicateSectionException;
import nextstep.subway.common.exception.InvalidDistanceException;
import nextstep.subway.common.exception.NotMatchStationException;
import nextstep.subway.station.domain.Station;

class LineTest {

	Line line;
	Station 신도림역;
	Station 잠실역;
	Station 서울대입구역;
	Station 낙성대역;
	Station 사당역;

	@BeforeEach
	void setup() {
		신도림역 = new Station("신도림역");
		잠실역 = new Station("잠실역");

		line = new Line("2호선", "green", 신도림역, 잠실역, 10);
		서울대입구역 = new Station("서울대입구역");
		낙성대역 = new Station("낙성대역");
		사당역 = new Station("사당역");

		line.addSection(사당역, 잠실역, 4);
		line.addSection(낙성대역, 사당역, 2);
		line.addSection(서울대입구역, 낙성대역, 2);
	}

	@DisplayName("라인에 포함되어 있는 station들을 가져온다.")
	@Test
	void 라인_역_찾기() {
		List<Station> stations = line.getStations();
		assertThat(stations).contains(
			신도림역,
			서울대입구역,
			낙성대역,
			사당역,
			잠실역
		);
	}

	@DisplayName("라인에 구간을 추가한다.")
	@Test
	void 라인_구간_추가() {
		Station 서초역 = new Station("서초역");
		line.addSection(사당역, 서초역, 1);

		List<Station> stations = line.getStations();
		assertThat(stations).contains(
			신도림역,
			서울대입구역,
			낙성대역,
			사당역,
			서초역,
			잠실역
		);

		Section section = line.getSections().findSectionByUpStation(사당역).orElse(null);
		assertThat(section.getDownStation()).isEqualTo(서초역);
	}

	@DisplayName("가운데 역이 추가가 된 경우, 역 사이의 거리는 업데이트 된다.")
	@Test
	void 라인_구간_거리_업데이트() {
		Station 서초역 = new Station("서초역");
		line.addSection(사당역, 서초역, 1);

		Section section = line.getSections().findSectionByUpStation(서초역).orElse(null);
		assertThat(section.getDownStation()).isEqualTo(잠실역);
		assertThat(section.getDistance()).isEqualTo(3);

	}

	@DisplayName("추가할려는 구간이 기존 구간 거리보다 긴 경우, 실패한다")
	@Test
	void 라인_구간_추가_실패_거리가_긴_경우() {
		Station 서초역 = new Station("서초역");

		assertThrows(InvalidDistanceException.class,
			() -> line.addSection(사당역, 서초역, 8)
		);
	}

	@DisplayName("추가할려는 구간이 기존 구간 거리보다 긴 경우, 실패한다")
	@Test
	void 라인_구간_추가_실패_이미_등록된_경우() {
		assertThrows(DuplicateSectionException.class,
			() -> line.addSection(낙성대역, 사당역, 3)
		);
	}

	@DisplayName("라인에 추가할려는 역이 둘 다 없는 경우, 실패한다")
	@Test
	void 라인_구간_추가_실패_등록할려는_역이_둘_다_없는_경우() {
		Station 서초역 = new Station("서초역");
		Station 교대역 = new Station("교대역");

		assertThrows(NotMatchStationException.class,
			() -> line.addSection(서초역, 교대역, 3)
		);
	}
}
