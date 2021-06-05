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
		Line line = new Line("5호선", "보라");
		Station station = new Station("종점역");
		when(stationService.findById(1L)).thenReturn(station);
		when(stationService.findById(2L)).thenThrow(IllegalArgumentException.class);

		// when then
		assertThatThrownBy(() -> lineService.saveLine(line, 1L, 2L, 1))
			.isInstanceOf(IllegalArgumentException.class);
	}
}