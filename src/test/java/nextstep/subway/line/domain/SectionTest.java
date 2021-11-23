package nextstep.subway.line.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간")
public class SectionTest {

	@DisplayName("구간을 생성한다.")
	@Test
	void of() {
		// given & when
		Section section = Section.of(강남역(), 역삼역(), 1);

		// then
		assertThat(section).isNotNull();
	}

	@DisplayName("상행역과 하행역이 같은 경우 구간을 생성할 수 없다.")
	@Test
	void ofFailIfUpStationAndDownStationIsEqual() {
		// given & when & then
		assertThatThrownBy(() -> Section.of(강남역(), 강남역(), 0))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("구간이 겹치는지 확인한다.")
	@Test
	void isOverlapped() {
		// given & when & then
		assertAll(
			() -> assertThat(Section.of(강남역(), 선릉역(), 1).isOverlapped(Section.of(강남역(), 역삼역(), 1))).isTrue(),
			() -> assertThat(Section.of(강남역(), 역삼역(), 1).isOverlapped(Section.of(강남역(), 선릉역(), 1))).isTrue(),
			() -> assertThat(Section.of(강남역(), 선릉역(), 1).isOverlapped(Section.of(역삼역(), 선릉역(), 1))).isTrue(),
			() -> assertThat(Section.of(역삼역(), 선릉역(), 1).isOverlapped(Section.of(강남역(), 선릉역(), 1))).isTrue(),
			() -> assertThat(Section.of(강남역(), 역삼역(), 1).isOverlapped(Section.of(역삼역(), 선릉역(), 1))).isFalse(),
			() -> assertThat(Section.of(역삼역(), 선릉역(), 1).isOverlapped(Section.of(강남역(), 역삼역(), 1))).isFalse()
		);
	}

	@DisplayName("기존 구간을 새로운 구간으로 나눈다. (상행이 같은 경우)")
	@Test
	void divideByOnUpStationSame() {
		// given
		Section givenSection = Section.of(강남역(), 선릉역(), 7);
		Section newSection = Section.of(강남역(), 역삼역(), 4);

		// when
		givenSection.divideBy(newSection);

		// then
		assertAll(
			() -> assertThat(givenSection.getUpStation()).isEqualTo(역삼역()),
			() -> assertThat(givenSection.getDownStation()).isEqualTo(선릉역()),
			() -> assertThat(givenSection.getDistance()).isEqualTo(3));
	}

	@DisplayName("기존 구간을 새로운 구간으로 나눈다. (하행이 같은 경우)")
	@Test
	void divideByOnDownStationSame() {
		// given
		Section givenSection = Section.of(강남역(), 선릉역(), 7);
		Section newSection = Section.of(역삼역(), 선릉역(), 3);

		// when
		givenSection.divideBy(newSection);

		// then
		assertAll(
			() -> assertThat(givenSection.getUpStation()).isEqualTo(강남역()),
			() -> assertThat(givenSection.getDownStation()).isEqualTo(역삼역()),
			() -> assertThat(givenSection.getDistance()).isEqualTo(4));
	}

	@DisplayName("기존 구간의 거리가 새로운 구간의 거리보다 작거나 같을 경우 나눌 수 없다.")
	@Test
	void divideByFail() {
		// given
		Section givenSection = Section.of(강남역(), 선릉역(), 7);
		Section newSection = Section.of(역삼역(), 선릉역(), 7);

		// when & then
		assertThatThrownBy(() -> givenSection.divideBy(newSection))
			.isInstanceOf(SectionAddFailException.class);
	}
}
