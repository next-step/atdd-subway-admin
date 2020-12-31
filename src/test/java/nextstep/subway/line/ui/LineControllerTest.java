package nextstep.subway.line.ui;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@ExtendWith(MockitoExtension.class)
class LineControllerTest {
	@Mock
	private LineService lineService;

	@DisplayName("라인생성: return BadRequest")
	@Test
	void createLineWithBadRequest() {
		// given
		when(lineService.saveLine(any())).thenThrow(RuntimeException.class);
		LineController lineController = new LineController(lineService);
		LineRequest request = new LineRequest("2호선", "green");

		// when
		ResponseEntity<LineResponse> response = lineController.createLine(request);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}
