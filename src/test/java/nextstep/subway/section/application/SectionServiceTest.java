package nextstep.subway.section.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

import java.util.Collections;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private SectionService sectionService;

    private static final Long 등록되지_않은_구간_ID = 0L;

    private static final Long 지하철_1호선_ID = 1L;
    private static final Long 구간_ID = 1L;

    private static final Long 신도림역_ID = 1L;
    private static final Long 서울역_ID = 2L;
    private static final Long 강남역_ID = 3L;

    private static final int 거리 = 10;

    private static final Station 신도림역 = new Station(신도림역_ID, "신도림역");
    private static final Station 서울역 = new Station(서울역_ID, "서울역");
    private static final Station 강남역 = new Station(강남역_ID, "강남역");

    private static final Line 지하철_1호선 = new Line(지하철_1호선_ID, "1호선", "남색", 신도림역, 서울역, 거리);

    private static final Section 구간 = new Section(지하철_1호선, 강남역, 서울역, 2);
    private static final SectionResponse 구간_반환_정보 = new SectionResponse(지하철_1호선_ID, 강남역_ID, 서울역_ID, 2);

    @DisplayName("모든 구간 정보를 반환한다")
    @Test
    void findAllSections() {
        when(sectionRepository.findAll()).thenReturn(Collections.singletonList(구간));

        assertThat(sectionService.findAllSections().get(0).getUpStationId()).isEqualTo(구간_반환_정보.getUpStationId());
        assertThat(sectionService.findAllSections().get(0).getDownStationId()).isEqualTo(구간_반환_정보.getDownStationId());
    }

    @DisplayName("구간 ID로 구간 정보를 반환한다")
    @Test
    void findSectionById() {
        when(sectionRepository.findById(구간_ID)).thenReturn(Optional.of(구간));

        assertThat(sectionService.findSectionById(구간_ID).getUpStationId()).isEqualTo(구간_반환_정보.getUpStationId());
        assertThat(sectionService.findSectionById(구간_ID).getDownStationId()).isEqualTo(구간_반환_정보.getDownStationId());
    }

    @DisplayName("구간 ID로 구간 정보를 불러온다")
    @Test
    void findSectionBySectionId() {
        when(sectionRepository.findById(구간_ID)).thenReturn(Optional.of(구간));

        assertThat(sectionService.findSectionBySectionId(구간_ID)).isSameAs(구간);
    }

    @DisplayName("등록되지 않은 구간 불러올 수 없다")
    @Test
    void findSectionBySectionId_NotExist_ExceptionThrown() {
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
            sectionService.findSectionBySectionId(등록되지_않은_구간_ID)
        );
    }

}
