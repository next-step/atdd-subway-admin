package nextstep.subway.section.ui;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.exception.SectionDuplicationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static nextstep.subway.common.exception.ErrorMessage.SECTION_DUPLICATION;
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
        Station upStation = new Station("강남역");
        Station downStation = new Station("잠실역");
        Line line = new Line("2호선", upStation, downStation);
        int distance = 10;

        when(sectionService.createSection(any(Long.class), any(SectionRequest.class)))
                .thenReturn(SectionResponse.of(new Section(line, upStation, downStation, distance)));

        mockMvc.perform(post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"downStationId\" : \"2\", \"upStationId\" : \"1\", \"distance\" : 10}"))
                .andExpect(jsonPath("distance").value(10))
                .andExpect(status().isCreated());
    }

    @DisplayName("중복된 구간을 생성요청하면 예외를 던진다")
    @Test
    void createSectionWithDuplication() throws Exception {
        when(sectionService.createSection(any(Long.class), any(SectionRequest.class)))
                .thenThrow(new SectionDuplicationException());

        mockMvc.perform(post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"downStationId\" : \"2\", \"upStationId\" : \"1\", \"distance\" : 10}"))
                .andExpect(jsonPath("message").value(SECTION_DUPLICATION.getMessage()))
                .andExpect(status().isBadRequest());
    }
}
