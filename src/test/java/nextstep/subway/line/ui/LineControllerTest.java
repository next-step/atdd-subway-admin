package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineControllerTest {
    @Mock
    private LineService lineService;

    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        when(lineService.save(lineRequest)).thenThrow(RuntimeException.class);
        LineController lineController = new LineController(lineService);

        // when
        ResponseEntity response = lineController.createLine(lineRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
