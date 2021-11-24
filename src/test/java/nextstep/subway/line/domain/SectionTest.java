package nextstep.subway.line.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

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

	@DisplayName("상행역 하행역을 가지는지 확인한다.")
	@Test
	public void containsAnyStation() {
		// given
		Section 강남_역삼_구간 = Section.of(강남역(), 역삼역(), 7);

		// when & then
		assertAll(
			() -> assertThat(강남_역삼_구간.containsAnyStation(강남역())).isTrue(),
			() -> assertThat(강남_역삼_구간.containsAnyStation(역삼역())).isTrue(),
			() -> assertThat(강남_역삼_구간.containsAnyStation(선릉역())).isFalse());
	}

	@DisplayName("상행역을 가지는지 확인한다.")
	@Test
	public void containsUpStation() {
		// given
		Section 강남_역삼_구간 = Section.of(강남역(), 역삼역(), 7);

		// when & then
		assertAll(
			() -> assertThat(강남_역삼_구간.containsUpStation(강남역())).isTrue(),
			() -> assertThat(강남_역삼_구간.containsUpStation(역삼역())).isFalse());
	}

	@DisplayName("하행역을 가지는지 확인한다.")
	@Test
	public void containsDownStation() {
		// given
		Section 강남_역삼_구간 = Section.of(강남역(), 역삼역(), 7);

		// when & then
		assertAll(
			() -> assertThat(강남_역삼_구간.containsDownStation(강남역())).isFalse(),
			() -> assertThat(강남_역삼_구간.containsDownStation(역삼역())).isTrue());
	}

	@DisplayName("현재 구간을 다른 구간과 합친다.")
	@Test
	public void merge() {
		// given
		Section firstSection = Section.of(1L, 강남역(), 역삼역(), 4);
		Section secondSection = Section.of(2L, 역삼역(), 선릉역(), 3);
		Sections sections = Sections.of(new ArrayList<>(Arrays.asList(firstSection, secondSection)));

		// when
		firstSection.merge(secondSection, sections);

		// then
		assertAll(
			() -> assertThat(sections.getValues()).doesNotContain(secondSection),
			() -> assertThat(sections.getValues().get(0).getDistance()).isEqualTo(7),
			() -> assertThat(sections.getValues().get(0).getUpStation()).isEqualTo(강남역()),
			() -> assertThat(sections.getValues().get(0).getDownStation()).isEqualTo(선릉역()));
	}
}
