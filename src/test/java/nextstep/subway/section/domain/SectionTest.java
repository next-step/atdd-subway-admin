package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.exception.CommonExceptionMessage.OVER_DISTANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

	private Station 강남역;
	private Station 구디역;
	private Station 사당역;

	@BeforeEach
	void setUp() {
		강남역 = new Station("강남역");
		구디역 = new Station("구디역");
		사당역 = new Station("사당역");
	}

	@Test
	@DisplayName("기존 구간 상행선에 새로운 역 연결")
	void connectUpStationToDownStationTest() {
		// given
		Section 강남구디_구간 = Section.of(강남역, 구디역, 10);
		Section 구디사당_구간 = Section.of(강남역, 사당역, 5);

		// when
		강남구디_구간.connectUpStationToDownStation(구디사당_구간);

		// then
		assertThat(강남구디_구간.upStation().getId()).isEqualTo(강남역.getId());
		assertThat(강남구디_구간.downStation().getId()).isEqualTo(사당역.getId());
	}

	@Test
	@DisplayName("기존 구간의 거리보다 큰 거리를 추가할 때 예외 테스트")
	void connectTestWithOverDistance() {
		// given
		Section 강남구디_구간 = Section.of(강남역, 구디역, 10);
		Section 구디사당_구간 = Section.of(강남역, 사당역, 15);

		// when
		assertThatThrownBy(() -> 강남구디_구간.connectUpStationToDownStation(구디사당_구간))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(OVER_DISTANCE.message());
	}

	@Test
	@DisplayName("기존 구간 하행선에 새로운 역 연결")
	void connectDownStationToUpStationTest() {
		// given
		Section 강남구디_구간 = Section.of(강남역, 구디역, 10);
		Section 사당구디_구간 = Section.of(사당역, 구디역, 5);

		// when
		강남구디_구간.connectDownStationToUpStation(사당구디_구간);

		// then
		assertThat(강남구디_구간.upStation().getId()).isEqualTo(사당역.getId());
		assertThat(강남구디_구간.downStation().getId()).isEqualTo(구디역.getId());
	}

	@Test
	@DisplayName("기존 구간중 하행선역을 제거")
	void disconnectDownStationTest() {
		// given
		Section 강남구디_구간 = Section.of(강남역, 구디역, 10);
		Section 구디사당_구간 = Section.of(구디역, 사당역, 5);

		// when
		구디사당_구간.disconnectDownStation(강남구디_구간);

		// then
		assertThat(구디사당_구간.upStation().getId()).isEqualTo(강남역.getId());
		assertThat(구디사당_구간.downStation().getId()).isEqualTo(사당역.getId());
	}
}
