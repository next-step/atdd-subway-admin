package nextstep.subway.line.ui;

import nextstep.subway.line.apllication.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
}
