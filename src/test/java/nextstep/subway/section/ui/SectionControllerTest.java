package nextstep.subway.section.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SectionControllerTest {

    private static final Long NOT_EXIST_ID = 0L;
    private static final Long EXIST_LINE_ID = 1L;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    @Autowired
    protected ObjectMapper objectMapper;

    private SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

    private SectionResponse sectionResponse = new SectionResponse(2L, 1L, 3L, 5);

    @Nested
    @DisplayName("POST /{lineId}/section는")
    class Describe_create_section {

        @Nested
        @DisplayName("유효한 구간이 주어지면")
        class Context_with_valid_section {
            final SectionRequest givenSectionRequest = sectionRequest;

            @BeforeEach
            void setUp() {
                when(sectionService.saveSection(anyLong(), any(SectionRequest.class)))
                        .thenReturn(sectionResponse);
            }

            @DisplayName("201 create를 응답한다.")
            @Test
            void It_responds_create() throws Exception {
                mockMvc.perform(
                        post("/lines/{lineId}/sections", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenSectionRequest))
                )
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("존재하지 않은 노선이 주어지면")
        class Context_with_not_existed_line {
            final SectionRequest givenSectionRequest = sectionRequest;

            @BeforeEach
            void setUp() {
                when(sectionService.saveSection(anyLong(), any(SectionRequest.class)))
                        .thenThrow(new LineNotFoundException());
            }

            @DisplayName("404 not found 응답한다.")
            @Test
            void It_responds_not_found() throws Exception {
                mockMvc.perform(
                        post("/{lineId}/sections", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenSectionRequest))
                )
                        .andExpect(status().isNotFound());
            }
        }
    }
}
