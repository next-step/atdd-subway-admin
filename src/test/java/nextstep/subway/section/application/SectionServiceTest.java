package nextstep.subway.section.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 관련 기능")
@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {
    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    SectionService sectionService;

    @DisplayName("구간을 저장한다.")
    @Test
    void saveSection() {
        // given
        Station upStation = new Station("대화");
        Station downStation = new Station("수서");
        Distance distance = Distance.valueOf(200);
        Section section = Section.valueOf(upStation, downStation, distance);

        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        // when
        Section savedSection = sectionService.saveSection(section);

        // then
        assertAll("validateValue",
            () -> Assertions.assertThat(savedSection.getUpStation()).isEqualTo(upStation),
            () -> Assertions.assertThat(savedSection.getDownStation()).isEqualTo(downStation),
            () -> Assertions.assertThat(savedSection.getDistance()).isEqualTo(distance)
        );
    }
}
