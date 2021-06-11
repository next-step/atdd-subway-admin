package nextstep.subway.line.domain;

import static org.mockito.Mockito.*;

import nextstep.subway.station.domain.Station;

public class StationTest {
	static Station 강남 = spy(new Station("강남"));
	static Station 양재 = spy(new Station("양재"));
	static Station 양재시민의숲 = spy(new Station("양재시민의숲"));
	static Station 청계산입구 = spy(new Station("청계산입구"));
	static Station 판교 = spy(new Station("판교"));
	static {
		when(강남.getId()).thenReturn(1L);
		when(양재.getId()).thenReturn(2L);
		when(양재시민의숲.getId()).thenReturn(3L);
		when(청계산입구.getId()).thenReturn(4L);
		when(판교.getId()).thenReturn(5L);
	}
}

