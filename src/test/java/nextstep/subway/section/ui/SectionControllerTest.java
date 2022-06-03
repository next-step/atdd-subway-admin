package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
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

@WebMvcTest(SectionController.class)
public class SectionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    @DisplayName("구간 생성한다")
    @Test
    void createSection() throws Exception {
        String downStationId = "2";
        String upStationId = "1";
        int distance = 10;

        when(sectionService.createSection(any(SectionRequest.class)))
                .thenReturn(SectionResponse.of(new Section(downStationId, upStationId, distance)));

        mockMvc.perform(post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"downStationId\" : \"2\", \"upStationId\" : \"1\", \"distance\" : 10}"))
                .andExpect(jsonPath("distance").value(10))
                .andExpect(status().isCreated());
    }
}
