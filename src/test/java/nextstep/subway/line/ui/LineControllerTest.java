package nextstep.subway.line.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineService lineService;

    @Test
    void createLine() throws Exception {

        // given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        LineResponse lineResponse = LineResponse.of(new Line("2호선", "green"));
        when(lineService.saveLine(any(LineRequest.class)))
                .thenReturn(lineResponse);

        // when
        // then
        mockMvc.perform(
                post("/lines")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lineRequest))
        ).andExpect(status().isCreated());
    }

    @Test
    void createDuplicatedLine() throws Exception {
        //given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        when(lineService.saveLine(any(LineRequest.class))).thenThrow(DataIntegrityViolationException.class);
        //when
        //then
        mockMvc.perform(
                post("/lines")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lineRequest))
        ).andExpect(status().isBadRequest());
    }
}