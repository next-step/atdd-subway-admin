package nextstep.subway.line.ui;

import nextstep.subway.line.apllication.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LineController.class)
public class LineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @DisplayName("지하철 노선 생성")
    @Test
    void create() throws Exception {
        Line line = new Line("2호선");
        when(lineService.create(any(LineRequest.class))).thenReturn(LineResponse.of(line));

        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"2호선\"}"))
                .andExpect(jsonPath("name").value("2호선"))
                .andExpect(status().isCreated());
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() throws Exception {
        List<Line> lines = Arrays.asList(new Line("2호선"), new Line("1호선"));

        when(lineService.getLines()).thenReturn(LineResponse.of(lines));

        mockMvc.perform(get("/lines"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk());
    }

    @DisplayName("id값이 존재한다면 지하철 노선 목록 조회")
    @Test
    void getLineWithValidId() throws Exception {
        Line line = new Line("2호선");
        when(lineService.getLine(1L)).thenReturn(LineResponse.of(line));

        mockMvc.perform(get("/lines/1"))
                .andExpect(jsonPath("name").value("2호선"))
                .andExpect(status().isOk());
    }

    @DisplayName("id값이 존재하지 않는다면 예외를 던진다")
    @Test
    void getLineWithInvalidId() throws Exception {
        when(lineService.getLine(1L)).thenThrow(new LineNotFoundException());

        mockMvc.perform(get("/lines/1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("id값이 존재한다면 지하철노선을 변경한다")
    @Test
    void updateLineWithValidId() throws Exception {
        Line line = new Line("2호선");
        when(lineService.getLine(1L)).thenReturn(LineResponse.of(line));

        mockMvc.perform(patch("/lines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"3호선\"}"))
                .andExpect(jsonPath("name").value("3호선"))
                .andExpect(status().isOk());
    }

    @DisplayName("id값이 존재하지 않는다면 예외를 던진다")
    @Test
    void updateLineWithInvalidId() throws Exception {
        when(lineService.getLine(1L)).thenThrow(new LineNotFoundException());

        mockMvc.perform(patch("/lines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"3호선\"}"))
                .andExpect(status().isNotFound());
    }
}
