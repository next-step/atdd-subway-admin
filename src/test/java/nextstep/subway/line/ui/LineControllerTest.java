package nextstep.subway.line.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineDuplicatedException;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        @DisplayName("유효한 노선이 주어지면")
        class Context_with_valid_line {
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

        @Nested
        @DisplayName("이미 존재하는 노선이 주어지면")
        class Context_with_existed_line {
            final LineRequest givenLineRequest = lineRequest;

            @BeforeEach
            void setUp() {
                when(lineService.saveLine(any(LineRequest.class)))
                        .thenThrow(new LineDuplicatedException());
            }

            @DisplayName("409 conflict를 응답한다.")
            @Test
            void It_responds_conflict() throws Exception {
                mockMvc.perform(
                        post("/lines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenLineRequest))
                )
                        .andExpect(status().isConflict());
            }
        }
    }

    @Nested
    @DisplayName("GET /lines는")
    class Describe_getLines {
        @Nested
        @DisplayName("등록된 노선이 있으면")
        class Context_with_lines {
            List<LineResponse> lines;

            @BeforeEach
            void setUp() {
                lines = Collections.singletonList(lineResponse);
                when(lineService.getLines())
                        .thenReturn(lines);
            }

            @DisplayName("200 OK 상태와 노선 목록을 응답한다.")
            @Test
            void It_responds_ok_with_lines() throws Exception {
                mockMvc.perform(
                        get("/lines")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andExpect(content().string(objectMapper.writeValueAsString(lines)));
            }
        }

        @Nested
        @DisplayName("등록된 노선이 없으면")
        class Context_without_lines {

            @DisplayName("200 OK 상태와 비어있는 노선 목록을 응답한다.")
            @Test
            void It_responds_ok_with_empty_lines() throws Exception {
                mockMvc.perform(get("/lines"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("[]")));
            }
        }
    }

}
