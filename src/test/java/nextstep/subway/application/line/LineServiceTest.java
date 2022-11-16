package nextstep.subway.application.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.application.stations.StationService;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineCreateRequest;
import nextstep.subway.dto.line.LineResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("지하철 노선 service 테스트")
class LineServiceTest {

	@Mock
	private StationService stationService;

	@Mock
	private LineRepository lineRepository;

	@InjectMocks
	private LineService lineService;

	@Test
	@DisplayName("지하철 노선 생성 테스트")
	void createLineTest() {
		// given
		final LineCreateRequest request = new LineCreateRequest("2호선", "green", 1L, 2L, 10);
		final Station upStation = Station.from("강남역");
		final Station downStation = Station.from("역삼역");
		given(stationService.findById(1L)).willReturn(upStation);
		given(stationService.findById(2L)).willReturn(downStation);
		given(lineRepository.save(any())).will(AdditionalAnswers.returnsFirstArg());

		// when
		LineResponse response = lineService.saveLine(request);

		// then
		assertAll(
			() -> assertThat(response.getName()).isEqualTo(request.getName()),
			() -> assertThat(response.getColor()).isEqualTo(request.getColor())
		);
	}
}