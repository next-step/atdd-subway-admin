package nextstep.subway.station.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("역")
class StationTest {

	@DisplayName("역을 생성한다.")
	@Test
	void of() {
		// given & when
		Station station = Station.of("강남역");

		// then
		assertThat(station).isNotNull();
	}

	@DisplayName("주어진 역들 중 처음인지 확인한다.")
	@Test
	void isHead() {
		// given
		Stations 강남_역삼_선릉 = Stations.of(Arrays.asList(강남역(), 역삼역(), 선릉역()));

		// when & then
		assertAll(
			() -> assertThat(강남역().isHeadOf(강남_역삼_선릉)).isTrue(),
			() -> assertThat(역삼역().isHeadOf(강남_역삼_선릉)).isFalse(),
			() -> assertThat(선릉역().isHeadOf(강남_역삼_선릉)).isFalse()
		);
	}

	@DisplayName("주어진 역들 중 끝인지 확인한다.")
	@Test
	void isTail() {
		// given
		Stations 강남_역삼_선릉 = Stations.of(Arrays.asList(강남역(), 역삼역(), 선릉역()));

		// when & then
		assertAll(
			() -> assertThat(강남역().isTailOf(강남_역삼_선릉)).isFalse(),
			() -> assertThat(역삼역().isTailOf(강남_역삼_선릉)).isFalse(),
			() -> assertThat(선릉역().isTailOf(강남_역삼_선릉)).isTrue()
		);
	}
}
