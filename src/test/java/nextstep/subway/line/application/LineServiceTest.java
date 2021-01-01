package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
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

@ExtendWith(MockitoExtension.class)
class LineServiceTest {
	@Mock
	private LineRepository lineRepository;

	private LineService lineService;

	@BeforeEach
	void setUp() {
		lineService = new LineService(lineRepository);
	}

	@DisplayName("지하철 노선 저장: DB Stubbing 테스트")
	@Test
	void saveTest() {
		// given
		LineRequest lineRequest = new LineRequest("2호선", "green");
		when(lineRepository.save(any())).thenReturn(new Line(1L, "2호선", "green"));

		// when
		LineResponse lineResponse = lineService.showLine(1L);

		// then
		assertThat(lineResponse).isNotNull();
		assertThat(lineResponse.getId()).isNotNull();
	}
}
