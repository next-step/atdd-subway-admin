package nextstep.subway.section.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationNotFoundException;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SectionControllerTest {

    private static final Long NOT_EXIST_LINE_ID = 0L;
    private static final Long NOT_EXIST_STATION_ID = 0L;
    private static final Long EXIST_LINE_ID = 1L;
    private static final Long EXIST_STATION_ID = 2L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    @Autowired
    protected ObjectMapper objectMapper;

    private SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

    private SectionResponse sectionResponse = new SectionResponse(1L, 3L, 5);

    @Nested
    @DisplayName("POST /{lineId}/section는")
    class Describe_create_section {

        @Nested
        @DisplayName("유효한 구간이 주어지면")
        class Context_with_valid_section {
            final SectionRequest givenSectionRequest = sectionRequest;

            @DisplayName("201 create를 응답한다.")
            @Test
            void It_responds_create() throws Exception {
                mockMvc.perform(
                        post("/lines/{lineId}/sections", EXIST_LINE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenSectionRequest))
                )
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("존재하지 않은 노선이 주어지면")
        class Context_with_not_existed_line {
            final SectionRequest givenSectionRequest = sectionRequest;

            @BeforeEach
            void setUp() {
                doThrow(new LineNotFoundException()).when(sectionService).saveSection(anyLong(), any(SectionRequest.class));
            }

            @DisplayName("404 not found 응답한다.")
            @Test
            void It_responds_not_found() throws Exception {
                mockMvc.perform(
                        post("/{lineId}/sections", EXIST_LINE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(givenSectionRequest))
                )
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("DELETE /lines/{lineId}/section?StationId={stationId}는")
    class Describe_delete_section {

        @Nested
        @DisplayName("제거할 노선과 역 식별자가 주어지면")
        class Context_with_valid_section {
            final Long givenLineId = EXIST_LINE_ID;
            final Long givenStationId = EXIST_STATION_ID;

            @DisplayName("204 No Content를 응답한다.")
            @Test
            void It_responds_no_content() throws Exception {
                mockMvc.perform(
                        delete("/lines/{lineId}/sections?stationId={stationId}", givenLineId, givenStationId)
                )
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 노선이 주어지면")
        class Context_with_invalid_line {
            final Long givenLineId = NOT_EXIST_LINE_ID;
            final Long givenStationId = EXIST_STATION_ID;

            @BeforeEach
            void setUp() {
                doThrow(new LineNotFoundException()).when(sectionService).deleteSection(anyLong(), anyLong());
            }

            @DisplayName("404 Not Found를 응답한다.")
            @Test
            void It_responds_no_content() throws Exception {
                mockMvc.perform(
                        delete("/lines/{lineId}/sections?stationId={stationId}", givenLineId, givenStationId)
                )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 지하철역이 주어지면")
        class Context_with_invalid_station {
            final Long givenLineId = NOT_EXIST_LINE_ID;
            final Long givenStationId = NOT_EXIST_STATION_ID;

            @BeforeEach
            void setUp() {
                doThrow(new StationNotFoundException()).when(sectionService).deleteSection(anyLong(), anyLong());
            }

            @DisplayName("404 Not Found를 응답한다.")
            @Test
            void It_responds_no_content() throws Exception {
                mockMvc.perform(
                        delete("/lines/{lineId}/sections?stationId={stationId}", givenLineId, givenStationId)
                )
                        .andExpect(status().isNotFound());
            }
        }

    }
}
