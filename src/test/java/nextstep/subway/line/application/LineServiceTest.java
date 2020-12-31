package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

	@Test
	void saveTest() {
		// given
		LineService lineService = new LineService(lineRepository);
		LineRequest lineRequest = new LineRequest("2호선", "green");
		when(lineRepository.save(any())).thenReturn(lineRepository.save(new Line("2호선", "green")));

		// when
		LineResponse lineResponse = lineService.saveLine(lineRequest);

		// then
		assertThat(lineResponse).isNotNull();
		assertThat(lineResponse.getId()).isNotNull();
	}
}
