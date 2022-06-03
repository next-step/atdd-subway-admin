package nextstep.subway.section.application;

import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.ui.SectionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {
    @Mock
    private SectionService sectionService;

    @Test
    void create() {
        SectionResponse sectionResponse = sectionService.createSection(
                SectionRequest.of("2", "1", 10));

        assertThat(sectionResponse.getDistance()).isEqualTo(10);
    }
}
