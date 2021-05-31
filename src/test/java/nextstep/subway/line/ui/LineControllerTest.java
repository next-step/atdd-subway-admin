package nextstep.subway.line.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineDuplicatedException;
import nextstep.subway.line.application.LineNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {
    private static final Long NOT_EXIST_ID = 0L;
    private static final Long EXIST_LINE_ID = 1L;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @Autowired
    protected ObjectMapper objectMapper;

    private LineRequest lineRequest = new LineRequest("2호선", "green");
    private LineRequest updateRequest = new LineRequest("3호선", "orange");
    private LineResponse lineResponse = LineResponse.of(new Line("2호선", "green"));
    private LineResponse updateResponse = LineResponse.of(new Line("3호선", "orange"));

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

    @Nested
    @DisplayName("GET /lines/{id} 는")
    class Describe_getLine {

        @Nested
        @DisplayName("등록된 노선이 없으면")
        class Context_without_line {
            @BeforeEach
            void setUp() {
                when(lineService.getLine(eq(NOT_EXIST_ID)))
                        .thenThrow(new LineNotFoundException());
            }

            @DisplayName("404 Not Found 상태를 응답한다.")
            @Test
            void It_responds_not_found() throws Exception {
                mockMvc.perform(get("/lines/{id}", NOT_EXIST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("등록된 노선이 있으면")
        class Context_with_line {
            final LineResponse givenLine = lineResponse;

            @BeforeEach
            void setUp() {
                when(lineService.getLine(anyLong()))
                        .thenReturn(givenLine);
            }

            @DisplayName("200 OK 상태와 찾고자 하는 노선을 응답한다.")
            @Test
            void it_responds_ok_with_line() throws Exception {
                mockMvc.perform(get("/lines/{id}", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string(objectMapper.writeValueAsString(givenLine)));
            }
        }
    }

    @Nested
    @DisplayName("PUT /lines/{id} 는")
    class Describe_updateLine {

        @Nested
        @DisplayName("갱신대상인 노선이 없으면")
        class Context_without_line {
            final LineRequest givenRequest = updateRequest;

            @BeforeEach
            void setUp() {
                doThrow(new LineNotFoundException())
                        .when(lineService)
                        .updateLine(eq(NOT_EXIST_ID), any(LineRequest.class));
            }

            @DisplayName("404 상태코드, Not Found 상태를 응답한다.")
            @Test
            void It_responds_not_found() throws Exception {
                mockMvc.perform(put("/line/{id}", NOT_EXIST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("갱신대상인 노선이 존재하면")
        class Context_with_line {
            final LineRequest givenRequest = updateRequest;

            @DisplayName("200 OK 상태를 응답한다.")
            @Test
            void It_responds_ok() throws Exception {
                mockMvc.perform(put("/lines/{id}", EXIST_LINE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isOk());
            }
        }
    }
}
