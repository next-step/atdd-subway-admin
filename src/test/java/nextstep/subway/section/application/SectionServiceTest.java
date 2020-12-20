package nextstep.subway.section.application;

import nextstep.subway.line.domain.LineFixtures;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.StationFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {
    private SectionService sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @BeforeEach
    void setup() {
        sectionService = new SectionService(sectionRepository);
    }

    @DisplayName("Section을 저장할 수 있다.")
    @Test
    void saveSectionTest() {
        Long distance = 3L;
        Long downStationId = 1L;
        Long upStationId = 2L;
        Section mockSection = new Section(upStationId, downStationId, distance);

        sectionService.createNewSection(mockSection);

        verify(sectionRepository).save(any(Section.class));
    }
}