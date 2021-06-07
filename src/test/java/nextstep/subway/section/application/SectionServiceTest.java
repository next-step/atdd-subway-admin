package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    private SectionService sectionService;

    private Line line;

    private Station station1;
    private Station station2;
    private Station station3;

    private SectionRequest sectionRequest;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(lineRepository, stationService);
        station1 = new Station("강남역");
        station2 = new Station("잠실역");
        station3 = new Station("선릉역");
        Section section = new Section(station1, station2, 10);
        line = new Line("이호선", "green", section);
        sectionRequest = new SectionRequest(1L, 3L, 5);
    }

    @Test
    void createSection() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.of(line));
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(station1));
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(station2));

        SectionResponse response = sectionService.saveSection(anyLong(), sectionRequest);

        assertThat(response).extracting("distance").isEqualTo(5);
        assertThat(response).extracting("upStationId").isEqualTo(1L);
        assertThat(response).extracting("downStationId").isEqualTo(3L);
    }
}
