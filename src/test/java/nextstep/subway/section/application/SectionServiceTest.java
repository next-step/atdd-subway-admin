package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("SectionService는")
@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {
    @InjectMocks
    private SectionService sectionService;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @Test
    void create() {
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "잠실역");
        Section section = new Section(upStation, downStation, 10);
        Line line = new Line("2호선", "green", section);
        line.addSection(section);
        SectionRequest sectionRequest = SectionRequest.of(1L, 2L, 10);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findById(eq(1L))).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(eq(2L))).thenReturn(Optional.of(downStation));

        SectionResponse sectionResponse = sectionService.createSection(anyLong(), sectionRequest);

        assertThat(sectionRequest.getDistance()).isEqualTo(sectionResponse.getDistance());
    }
}
