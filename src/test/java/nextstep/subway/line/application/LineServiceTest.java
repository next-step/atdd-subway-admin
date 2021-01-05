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

import nextstep.subway.common.exception.ExistException;
import nextstep.subway.common.exception.NothingException;
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

	private Line line;
	private Station 강남역;
	private Station 서초역;
	private Station 홍대역;
	private Station 시청역;

	private LineRequest lineRequest;

	@BeforeEach
	void setUp() {
		lineService = new LineService(lineRepository, stationRepository);

		강남역 = new Station(1L, "강남역");
		서초역 = new Station(2L, "서초역");
		홍대역 = new Station(3L, "홍대역");
		시청역 = new Station(4L, "시청역");

		line = new Line(1L, "2호선", "green", 강남역, 서초역, 100);
		lineRequest = new LineRequest(line.getName(), line.getColor(), 서초역.getId(), 강남역.getId(), 100);
	}

	@DisplayName("지하철 노선 조회: DB Stubbing 테스트")
	@Test
	void saveTest() {
		// given
		when(lineRepository.findById(any())).thenReturn(java.util.Optional.of(line));

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
		when(lineRepository.save(any())).thenReturn(line);
		when(stationRepository.findById(강남역.getId())).thenReturn(java.util.Optional.of(강남역));
		when(stationRepository.findById(서초역.getId())).thenReturn(java.util.Optional.of(서초역));

		// when
		LineResponse response = lineService.saveLine(lineRequest);

		// then
		assertAll(
			() -> assertThat(response.getName()).isEqualTo("2호선")
		);
	}

	@DisplayName("지하철 노선 추가 예외: 상행,하행역 모두 미포함 구간추가 예외")
	@Test
	void AddSectionNothingExceptionTest() {
		// given
		LineResponse response = 지하철_2호선_생성();
		when(stationRepository.findById(홍대역.getId())).thenReturn(java.util.Optional.of(홍대역));
		when(stationRepository.findById(시청역.getId())).thenReturn(java.util.Optional.of(시청역));
		SectionRequest request = new SectionRequest(홍대역.getId(), 시청역.getId(), 20);

		// when //then
		assertThatThrownBy(
			() -> lineService.addSection(response.getId(), request)
		).isInstanceOf(NothingException.class);
	}

	@DisplayName("지하철 노선 추가 예외: 상행,하행역 모두 포함 구간추가 예외")
	@Test
	void AddSectionExistExceptionTest() {
		// given
		LineResponse response = 지하철_2호선_생성();
		SectionRequest request = new SectionRequest(강남역.getId(), 서초역.getId(), 20);

		// when //then
		assertThatThrownBy(
			() -> lineService.addSection(response.getId(), request)
		).isInstanceOf(ExistException.class);
	}

	private void setStubbing() {
		when(lineRepository.save(any())).thenReturn(line);
		when(lineRepository.findById(any())).thenReturn(java.util.Optional.of(line));
		when(stationRepository.findById(서초역.getId())).thenReturn(java.util.Optional.of(서초역));
		when(stationRepository.findById(강남역.getId())).thenReturn(java.util.Optional.of(강남역));
	}

	private LineResponse 지하철_2호선_생성() {
		setStubbing();
		return lineService.saveLine(lineRequest);
	}
}
