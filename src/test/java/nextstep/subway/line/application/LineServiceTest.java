package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

	@Mock
	private LineRepository lineRepository;

	@Mock
	private StationService stationService;

	@InjectMocks
	private LineService lineService;

	@Test
	@DisplayName("노선 저장시 종점역 정보가 없으면 저장할 수 없다.")
	void saveLineTestWithNonExistsStation() {
		// given
		LineRequest lineRequest = mock(LineRequest.class);
		when(lineRequest.getUpStationId()).thenReturn(1L);
		when(lineRequest.getDownStationId()).thenReturn(2L);
		when(stationService.findById(1L)).thenReturn(new Station("강남역"));
		when(stationService.findById(2L)).thenThrow(IllegalArgumentException.class);

		// when then
		assertThatThrownBy(() -> lineService.saveLine(lineRequest))
			.isInstanceOf(IllegalArgumentException.class);
	}
}