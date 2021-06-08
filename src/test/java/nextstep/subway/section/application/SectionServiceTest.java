package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private Line line;

    private Line line2;

    private Station station1;
    private Station station2;
    private Station station3;

    private SectionRequest sectionRequest;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(lineRepository, stationService);
        station1 = new Station("강남역");
        ReflectionTestUtils.setField(station1, "id", 1L);

        station2 = new Station("잠실역");
        ReflectionTestUtils.setField(station2, "id", 2L);

        station3 = new Station("선릉역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        Section section = new Section(station1, station2, 10);
        line = new Line("이호선", "green", section);
        sectionRequest = new SectionRequest(1L, 3L, 5);
    }

    @Test
    void createSection() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.of(line));
        when(stationRepository.findById(eq(1L)))
                .thenReturn(Optional.of(station1));
        when(stationRepository.findById(eq(3L)))
                .thenReturn(Optional.of(station3));

        assertDoesNotThrow(() -> sectionService.saveSection(anyLong(), sectionRequest));
    }
}
