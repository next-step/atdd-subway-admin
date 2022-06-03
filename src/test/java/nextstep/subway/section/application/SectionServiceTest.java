package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {
    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @Test
    void create() {
        String downStationId = "2";
        String upStationId = "1";
        int distance = 10;

        Section section = new Section(downStationId, upStationId, distance);
        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        SectionResponse sectionResponse = sectionService.createSection(
                SectionRequest.of(downStationId, upStationId, distance));

        assertThat(sectionResponse.getDistance()).isEqualTo(distance);
    }
}
