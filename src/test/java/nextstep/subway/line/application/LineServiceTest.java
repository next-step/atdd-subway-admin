package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
	@Mock
	private LineRepository lineRepository;

	@Mock
	private StationRepository stationRepository;

	private LineService lineService;

	@BeforeEach
	void setUp() {
		lineService = new LineService(lineRepository, stationRepository);
	}

	@DisplayName("지하철 노선 조회: DB Stubbing 테스트")
	@Test
	void saveTest() {
		// given
		when(lineRepository.findById(any())).thenReturn(java.util.Optional.of(new Line(1L, "2호선", "green")));

		// when
		LineResponse lineResponse = lineService.showLine(1L);

		// then
		assertThat(lineResponse).isNotNull();
		assertThat(lineResponse.getId()).isNotNull();
	}

	@DisplayName("지하철 노선 저장: section 추가된 노선 저장 테스트")
	@Test
	void saveLineAddSectionTest() {
		// given
		LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 100);
		when(lineRepository.save(any())).thenReturn(new Line(1L, "2호선", "green"));
		when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(new Station(1L, "서울역")));
		when(stationRepository.findById(2L)).thenReturn(java.util.Optional.of(new Station(2L, "강남역")));

		// when
		LineResponse response = lineService.saveLine(request);

		// then
		assertAll(
			() -> assertThat(response.getName()).isEqualTo("2호선")
		);
	}

	@DisplayName("지하철 노선 추가: 기존 노선에 section 추가 테스트 ")
	@Test
	void AddSectionAboutLineTest() {
		// given
		LineResponse response = 지하철_2호선_생성();
		SectionRequest request = new SectionRequest(3L, 4L, 20);
		when(stationRepository.findById(3L)).thenReturn(java.util.Optional.of(new Station(3L, "홍대역")));
		when(stationRepository.findById(4L)).thenReturn(java.util.Optional.of(new Station(4L, "서초역")));

		// when
		LineResponse lineResponse = lineService.addSection(response.getId(), request);

		// then
		assertAll(
			() -> assertThat(response.getName()).isEqualTo("2호선")
		);
	}

	private LineResponse 지하철_2호선_생성() {
		when(lineRepository.save(any())).thenReturn(new Line(1L, "2호선", "green"));
		when(lineRepository.findById(any())).thenReturn(java.util.Optional.of(new Line(1L, "2호선", "green")));
		when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(new Station(1L, "서초역")));
		when(stationRepository.findById(2L)).thenReturn(java.util.Optional.of(new Station(2L, "강남역")));
		return lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 100));
	}
}
