package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineControllerTest {
    @Mock
    private LineService lineService;

    @Test
    void createLineWithBadRequest() {
        when(lineService.save(any())).thenThrow(RuntimeException.class);
        LineController lineController = new LineController(lineService);

        LineRequest lineRequest = new LineRequest("2호선", "green");
        ResponseEntity responseEntity = lineController.createLine(lineRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}