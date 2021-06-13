package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.LineTest.*;
import static nextstep.subway.line.domain.SectionTest.*;
import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {

	@Test
	@DisplayName("구간 사이의 역들을 상행 -> 하행 방향으로 정렬할 수 있다.")
	void orderStationsTest() {
		// given
		Sections 정렬되지_않은_강남에서_판교까지_구간들 = Sections.of(양재_양재시민의숲_구간, 강남_양재_구간, 청계산입구_판교_구간, 양재시민의숲_청계산입구_구간);

		// when
		List<Station> orderedStations = 정렬되지_않은_강남에서_판교까지_구간들.orderedStations();

		// then
		assertThat(orderedStations).containsExactly(강남, 양재, 양재시민의숲, 청계산입구, 판교);
	}

	@Test
	@DisplayName("구간들에 이미 있는 구간은 추가할 수 없다.")
	void addSectionAlreadyExist() {
		// given
		Sections 강남_양재_양재시민의숲 = Sections.of(양재_양재시민의숲_구간, 강남_양재_구간);
		Section 강남_양재 = new Section(신분당선, 강남, 양재, Distance.valueOf(10));
		Section 강남_양재시민의숲 = new Section(신분당선, 강남, 양재시민의숲, Distance.valueOf(10));

		// when then
		assertAll(
			() -> assertThatThrownBy(() -> 강남_양재_양재시민의숲.addSection(강남_양재))
				.isInstanceOf(IllegalArgumentException.class),
			() -> assertThatThrownBy(() -> 강남_양재_양재시민의숲.addSection(강남_양재시민의숲))
				.isInstanceOf(IllegalArgumentException.class)
		);
	}

	@Test
	@DisplayName("상행역 하행역 모두 노선에 존재하지 않는 구간은 등록할 수 없다.")
	void addSectionNotExist() {
		// given
		Sections 강남_양재_양재시민의숲 = Sections.of(강남_양재_구간);

		// when
		assertThatThrownBy(() -> 강남_양재_양재시민의숲.addSection(양재시민의숲_청계산입구_구간))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("상행역 하행역 모두 노선에 존재하지 않는 구간은 등록할 수 없다.");
	}

	@Test
	@DisplayName("역 사이에 역을 추가할 수 있다.")
	void addSectionBetweenStation() {
		// given
		Section 강남_청계산입구 = spy(new Section(신분당선, 강남, 청계산입구, Distance.valueOf(10)));
		when(강남_청계산입구.getId()).thenReturn(5L);
		Section 양재시민의숲_청계산입구 = spy(new Section(신분당선, 양재시민의숲, 청계산입구, Distance.valueOf(5)));
		when(양재시민의숲_청계산입구.getId()).thenReturn(6L);
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(4)));
		when(강남_양재.getId()).thenReturn(7L);
		Sections 강남_청계산입구_구간들 = Sections.of(강남_청계산입구);

		// when
		강남_청계산입구_구간들.addSection(양재시민의숲_청계산입구);
		강남_청계산입구_구간들.addSection(강남_양재);

		// then
		assertThat(강남_청계산입구_구간들.orderedStations()).containsExactly(강남, 양재, 양재시민의숲, 청계산입구);
	}

	@Test
	@DisplayName("상행 종점으로 역을 추가할 수 있다.")
	void addSectionToHead() {
		// given
		Section 양재_양재시민의숲 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(10)));
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(5)));
		Sections 강남_양재시민의숲_구간들 = Sections.of(양재_양재시민의숲);

		// when
		강남_양재시민의숲_구간들.addSection(강남_양재);

		// then
		assertThat(강남_양재시민의숲_구간들.orderedStations()).containsExactly(강남, 양재, 양재시민의숲);
	}

	@Test
	@DisplayName("하행 종점으로 역을 추가할 수 있다.")
	void addSectionToTail() {
		// given
		Section 양재_양재시민의숲 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(10)));
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(5)));
		Sections 강남_양재시민의숲_구간들 = Sections.of(강남_양재);

		// when
		강남_양재시민의숲_구간들.addSection(양재_양재시민의숲);

		// then
		assertThat(강남_양재시민의숲_구간들.orderedStations()).containsExactly(강남, 양재, 양재시민의숲);
	}

	@Test
	@DisplayName("종점이 제거되면 다음으로 오던 역이 종점이 된다.")
	void removeEndStationTest() {
		// given
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(5)));
		when(강남_양재.getId()).thenReturn(1L);
		Section 양재_양재시민의숲 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(10)));
		when(양재_양재시민의숲.getId()).thenReturn(2L);
		Sections 강남_양재시민의숲_구간들 = Sections.of(강남_양재, 양재_양재시민의숲);

		// when
		강남_양재시민의숲_구간들.removeSectionBy(강남);

		// then
		assertThat(강남_양재시민의숲_구간들.orderedStations()).containsExactly(양재, 양재시민의숲);
		assertThat(강남_양재시민의숲_구간들.sumDistance()).isEqualTo(Distance.valueOf(10));
	}

	@Test
	@DisplayName("중간역이 제거되면 제거되는 역의 상행역-하행역이 이어진다.")
	void removeInnerStationTest() {
		// given
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(4)));
		when(강남_양재.getId()).thenReturn(1L);
		Section 양재_양재시민의숲 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(5)));
		when(양재_양재시민의숲.getId()).thenReturn(2L);
		Sections 강남_양재시민의숲_구간들 = Sections.of(양재_양재시민의숲, 강남_양재);

		// when
		강남_양재시민의숲_구간들.removeSectionBy(양재);

		// then
		assertThat(강남_양재시민의숲_구간들.orderedStations()).containsExactly(강남, 양재시민의숲);
		assertThat(강남_양재시민의숲_구간들.sumDistance()).isEqualTo(Distance.valueOf(9));
	}

	@Test
	@DisplayName("노선에 없는 역의 구간은 제거할 수 없다.")
	void removeSectionByUnknownStationTest() {
		// given
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(4)));
		when(강남_양재.getId()).thenReturn(1L);
		Section 양재_양재시민의숲 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(5)));
		when(양재_양재시민의숲.getId()).thenReturn(2L);
		Sections 강남_청계산입구_구간들 = Sections.of(양재_양재시민의숲, 강남_양재);

		// when then
		assertThatThrownBy(() -> 강남_청계산입구_구간들.removeSectionBy(판교))
			.isInstanceOf(IllegalArgumentException.class)
		    .hasMessageContaining("노선에 없는 역의 구간은 제거할 수 없습니다.");
	}

	@Test
	@DisplayName("노선에 구간이 하나 밖에 없는 경우에는 구간을 제거 할 수 없다.")
	void removeLastSectionTest() {
		// given
		Section 강남_양재 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(4)));
		when(강남_양재.getId()).thenReturn(1L);
		Sections 강남_양재_구간들 = Sections.of(강남_양재);

		// when then
		assertThatThrownBy(() -> 강남_양재_구간들.removeSectionBy(강남))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("노선의 남은 구간이 하나 밖에 없어 제거할 수 없습니다.");
	}
}
