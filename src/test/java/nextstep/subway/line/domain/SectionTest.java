package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.LineTest.*;
import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {
	static Section 강남_양재_구간 = spy(new Section(신분당선, 강남, 양재, Distance.valueOf(10)));
	static Section 양재_양재시민의숲_구간 = spy(new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(10)));
	static Section 양재시민의숲_청계산입구_구간 = spy(new Section(신분당선, 양재시민의숲, 청계산입구, Distance.valueOf(10)));
	static Section 청계산입구_판교_구간 = spy(new Section(신분당선, 청계산입구, 판교, Distance.valueOf(10)));
	static {
		when(강남_양재_구간.getId()).thenReturn(1L);
		when(양재_양재시민의숲_구간.getId()).thenReturn(2L);
		when(양재시민의숲_청계산입구_구간.getId()).thenReturn(3L);
		when(청계산입구_판교_구간.getId()).thenReturn(4L);
	}

	@Test
	@DisplayName("구간의 상행역, 하행역에 따라서 이전 이후 역을 판별할 수 있다.")
	public void isPreviousAndNextTest() {
		assertAll(
			() -> assertThat(강남_양재_구간.isUpwardOf(양재_양재시민의숲_구간)).isTrue(),
			() -> assertThat(청계산입구_판교_구간.isDownwardOf(양재시민의숲_청계산입구_구간)).isTrue()
		);
	}

	@Test
	@DisplayName("역 사이에 역을 추가할 때, 추가하는 역의 구간 길이 만큼의 거리가 줄어든다.")
	void addSectionDistanceTest() {
		// given
		Section 강남_양재시민의숲 = new Section(신분당선, 강남, 양재시민의숲, Distance.valueOf(10));
		Section 양재_양재시민의숲 = new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(3));

		// when
		강남_양재시민의숲.addInnerSection(양재_양재시민의숲);

		// then
		assertThat(강남_양재시민의숲.getDistance()).isEqualTo(Distance.valueOf(7));
		assertThat(강남_양재시민의숲.getStreamOfStations()).containsExactly(강남, 양재);
	}

	@ParameterizedTest
	@DisplayName("역 사이에 역을 추가할 때, 추가하는 구간의 길이가 기존 구간의 길이보다 같거나 크면 추가할 수 없다.")
	@ValueSource(ints = {10, 11})
	public void addSectionOverDistance(int longDistance) {
		// given
		Section 강남_양재시민의숲 = new Section(신분당선, 강남, 양재시민의숲, Distance.valueOf(10));
		Section 강남_양재 = new Section(신분당선, 강남, 양재, Distance.valueOf(longDistance));

		// when then
		assertThatThrownBy(() -> 강남_양재시민의숲.addInnerSection(강남_양재))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("추가할 구간이 기존 구간 보다 같거나 길 수 없습니다.");
	}

	@Test
	@DisplayName("두 구간을 연결하는 역을 삭제하면, 두 구간이 합쳐진다.")
	public void removeConnectingStationTest() {
		Section 강남_양재 = new Section(신분당선, 강남, 양재, Distance.valueOf(5));
		Section 양재_양재시민의숲 = new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(6));

		강남_양재.removeConnectingStation(양재, 양재_양재시민의숲);

		assertThat(강남_양재.getStreamOfStations()).containsExactly(강남, 양재시민의숲);
		assertThat(강남_양재.getDistance()).isEqualTo(Distance.valueOf(11));
	}

	@Test
	@DisplayName("두 구간을 연결하는 역이 아닌 역을 삭제하면, 삭제되지 않는다.")
	public void removeEndStationTest() {
		Section 강남_양재 = new Section(신분당선, 강남, 양재, Distance.valueOf(5));
		Section 양재_양재시민의숲 = new Section(신분당선, 양재, 양재시민의숲, Distance.valueOf(6));

		boolean isRemoved = 강남_양재.removeConnectingStation(강남, 양재_양재시민의숲);

		assertThat(isRemoved).isFalse();
	}
}