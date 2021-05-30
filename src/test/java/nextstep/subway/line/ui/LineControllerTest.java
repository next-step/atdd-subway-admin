package nextstep.subway.line.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @Autowired
    protected ObjectMapper objectMapper;

    private LineRequest lineRequest = new LineRequest("2호선", "green");
    private LineResponse lineResponse = LineResponse.of(new Line("2호선", "green"));

    @Nested
    @DisplayName("POST /lines는")
    class Describe_create_line {

        @Nested
        @DisplayName("노선이 주어지면")
        class Context_with_line {
            final LineRequest givenLineRequest = lineRequest;

            @BeforeEach
            void setUp() {
                when(lineService.saveLine(any(LineRequest.class)))
                        .thenReturn(lineResponse);
            }

            @DisplayName("201 create를 응답한다.")
            @Test
            void It_responds_create() throws Exception {
                mockMvc.perform(
                        post("/lines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenLineRequest))
                )
                        .andExpect(status().isCreated());
            }
        }
    }

}
