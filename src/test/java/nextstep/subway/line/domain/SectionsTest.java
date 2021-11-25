package nextstep.subway.line.domain;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.SectionFixture;
import nextstep.subway.station.StationFixture;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

@DisplayName("구간들")
class SectionsTest {

	@DisplayName("구간들을 생성한다.")
	@Test
	void of() {
		// given
		List<Section> values = Arrays.asList(
			SectionFixture.강남역_역삼역_구간(),
			SectionFixture.역삼역_선릉역_구간(),
			SectionFixture.선릉역_삼성역_구간());

		// when
		Sections sections = Sections.of(values);

		// then
		assertAll(
			() -> assertThat(sections).isNotNull(),
			() -> assertThat(sections.size()).isEqualTo(values.size())
		);
	}

	@DisplayName("구간들 내 역들을 상행역부터 하행역순으로 가져온다.")
	@Test
	void getStationsInOrder() {
		// given
		Sections sections = Sections.of(Arrays.asList(
			SectionFixture.선릉역_삼성역_구간(),
			SectionFixture.강남역_역삼역_구간(),
			SectionFixture.역삼역_선릉역_구간()));

		// when
		Stations stations = sections.getStationsInOrder();

		// then
		assertThat(stations.getValues()).isEqualTo(Arrays.asList(
			StationFixture.강남역(),
			StationFixture.역삼역(),
			StationFixture.선릉역(),
			StationFixture.삼성역()));
	}

	@DisplayName("구간 거리들을 상행역부터 하행역순으로 가져온다.")
	@Test
	void getDistancesInOrder() {
		// given
		Sections sections = Sections.of(Arrays.asList(
			SectionFixture.선릉역_삼성역_구간(),
			SectionFixture.강남역_역삼역_구간(),
			SectionFixture.역삼역_선릉역_구간()));

		// when
		List<Integer> distances = sections.getDistancesInOrder();

		// then
		assertThat(distances).isEqualTo(Arrays.asList(
			SectionFixture.강남역_역삼역_구간().getDistance(),
			SectionFixture.역삼역_선릉역_구간().getDistance(),
			SectionFixture.선릉역_삼성역_구간().getDistance()));
	}

	@DisplayName("구간들이 비어있을 경우, 구간을 등록한다.")
	@Test
	void addOnEmpty() {
		// given
		Sections sections = Sections.of(new ArrayList<>());

		// when
		sections.add(SectionFixture.강남역_역삼역_구간());

		// then
		assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
			StationFixture.강남역(),
			StationFixture.역삼역()));
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을 때, 구간을 등록할 수 없다.")
	@Test
	void addFailOnBothStationsNotRegistered() {
		// given
		Sections sections = Sections.of(new ArrayList<>(Collections.singletonList(SectionFixture.강남역_역삼역_구간())));

		// when & then
		assertThatThrownBy(() -> sections.add(SectionFixture.선릉역_삼성역_구간()))
			.isInstanceOf(SectionAddFailException.class);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있을 때, 구간을 등록할 수 없다.")
	@Test
	void addFailOnBothStationsAlreadyRegistered() {
		// given
		Sections sections = Sections.of(new ArrayList<>(Collections.singletonList(SectionFixture.강남역_역삼역_구간())));

		// when & then
		assertThatThrownBy(() -> sections.add(SectionFixture.강남역_역삼역_구간()))
			.isInstanceOf(SectionAddFailException.class);
	}

	@DisplayName("역 사이 왼쪽에 새로운 구간을 등록한다.")
	@Test
	void addBetweenStationsAtLeftSide() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.강남역(), StationFixture.선릉역(), 7))));

		// when
		sections.add(Section.of(2L, StationFixture.강남역(), StationFixture.역삼역(), 4));

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.역삼역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Arrays.asList(4, 3)));
	}

	@DisplayName("역 사이 오른쪽에 새로운 구간을 등록한다.")
	@Test
	void addBetweenStationsAtRightSide() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.강남역(), StationFixture.선릉역(), 7))));

		// when
		sections.add(Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 3));

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.역삼역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Arrays.asList(4, 3)));
	}

	@DisplayName("역 사이 왼쪽에 새로운 구간을 등록할 때, 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
	@Test
	void addBetweenStationsAtLeftSideFailOnIllegalDistance() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.강남역(), StationFixture.선릉역(), 7))));

		// when & then
		assertThatThrownBy(
			() -> sections.add(Section.of(2L, StationFixture.강남역(), StationFixture.역삼역(), 7)))
			.isInstanceOf(SectionAddFailException.class);
	}

	@DisplayName("역 사이 오른쪽에 새로운 구간을 등록할 때, 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
	@Test
	void addBetweenStationsAtRightSideFailOnIllegalDistance() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.강남역(), StationFixture.선릉역(), 7))));

		// when & then
		assertThatThrownBy(
			() -> sections.add(Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 7)))
			.isInstanceOf(SectionAddFailException.class);
	}

	@DisplayName("상행 종점에 새로운 구간을 등록한다.")
	@Test
	void addToUpStation() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.역삼역(), StationFixture.선릉역(), 7))));

		// when
		sections.add(Section.of(2L, StationFixture.강남역(), StationFixture.역삼역(), 4));

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.역삼역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Arrays.asList(4, 7)));
	}

	@DisplayName("하행 종점에 새로운 구간을 등록한다.")
	@Test
	void addToDownStation() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(Section.of(1L, StationFixture.강남역(), StationFixture.역삼역(), 7))));

		// when
		sections.add(Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 3));

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.역삼역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Arrays.asList(7, 3)));
	}

	@DisplayName("지하철 구간을 삭제한다. (세 역 중 첫번째 역이 제거될 경우)")
	@Test
	void removeByFirstStation() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Arrays.asList(
				Section.of(1L, StationFixture.강남역(), StationFixture.역삼역(), 4),
				Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 3))));

		// when
		sections.removeByStation(StationFixture.강남역());

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.역삼역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Collections.singletonList(3)));
	}

	@DisplayName("지하철 구간을 삭제한다. (세 역 중 두번째 역이 제거될 경우)")
	@Test
	void removeBySecondStation() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Arrays.asList(
				Section.of(1L, StationFixture.강남역(), StationFixture.역삼역(), 4),
				Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 3))));

		// when
		sections.removeByStation(StationFixture.역삼역());

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.선릉역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Collections.singletonList(7)));
	}

	@DisplayName("지하철 구간을 삭제한다. (세 역 중 세번째 역이 제거될 경우)")
	@Test
	void removeByThirdStation() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Arrays.asList(
				Section.of(1L, StationFixture.강남역(), StationFixture.역삼역(), 4),
				Section.of(2L, StationFixture.역삼역(), StationFixture.선릉역(), 3))));

		// when
		sections.removeByStation(StationFixture.선릉역());

		// then
		assertAll(
			() -> assertThat(sections.getStationsInOrder().getValues()).isEqualTo(Arrays.asList(
				StationFixture.강남역(),
				StationFixture.역삼역())),
			() -> assertThat(sections.getDistancesInOrder()).isEqualTo(Collections.singletonList(4)));
	}

	@DisplayName("구간들에서 구간이 하나인 경우 마지막 구간은 제거할 수 없다.")
	@Test
	void removeStationFail() {
		// given
		Sections sections = Sections.of(
			new ArrayList<>(Collections.singletonList(
				Section.of(1L, StationFixture.강남역(), StationFixture.역삼역(), 4))));

		// when & then
		assertThatThrownBy(() -> sections.removeByStation(StationFixture.강남역()))
			.isInstanceOf(SectionRemoveFailException.class);
	}
}
