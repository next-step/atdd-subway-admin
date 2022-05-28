package nextstep.subway.line.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class LineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("지하철 노선 생성")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"2호선\"}"))
                .andExpect(jsonPath("name").value("2호선"))
                .andExpect(status().isCreated());
    }
}
