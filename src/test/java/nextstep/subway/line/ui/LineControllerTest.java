package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {
    @Mock
    private LineService lineService;

    @Test
    void createLineWithBadRequest() {
        // given
        LineResponse response = new LineResponse(1L, "신분당선", "red", LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        when(lineService.save(any())).thenReturn(response);
        LineController controller = new LineController(lineService);

        // when
        ResponseEntity responseEntity = controller.createLine(new LineRequest("신분당선", "red", 1L, 2L, 10));

        // given
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        LineResponse actual = (LineResponse) responseEntity.getBody();
        assertThat(actual.getName()).isEqualTo(response.getName());
        assertThat(actual.getColor()).isEqualTo(response.getColor());
    }
}