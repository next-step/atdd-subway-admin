package nextstep.subway.section.ui;

import nextstep.subway.section.ui.SectionController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionController.class)
public class SectionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("구간 생성한다")
    @Test
    void createSection() throws Exception {
        mockMvc.perform(post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"downStationId\" : \"2\", \"upStationId\" : \"1\", \"distance\" : 10}"))
                .andExpect(jsonPath("distance").value(10))
                .andExpect(status().isCreated());
    }
}
