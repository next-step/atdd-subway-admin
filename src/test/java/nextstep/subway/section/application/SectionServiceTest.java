package nextstep.subway.section.application;

import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {
    private static final Long LINE_ID = 1L;
    private static final Long STATION_ID = 3L;
    private static final Long NOT_EXIST_STATION_ID = 0L;

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

    private Section section;
    private Section section2;

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

        section = new Section(station1, station2, 20);
        section2 = new Section(station1, station3, 5);
        line = new Line("이호선", "green", section);
        line2 = new Line("이호선", "green", section);
        line.addSection(section2);
        sectionRequest = new SectionRequest(1L, 3L, 5);

    }

    @Test
    void createSection() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.of(line2));
        when(stationRepository.findById(eq(1L)))
                .thenReturn(Optional.of(station1));
        when(stationRepository.findById(eq(3L)))
                .thenReturn(Optional.of(station3));

        assertDoesNotThrow(() -> sectionService.saveSection(anyLong(), sectionRequest));
    }

    @DisplayName("유효한 구간 제거")
    @Test
    void deleteSection() {
        when(lineRepository.findById(anyLong()))
                .thenReturn(Optional.of(line));

        // when
        assertDoesNotThrow(() -> sectionService.deleteSection(anyLong(), STATION_ID));
    }

    @DisplayName("존재하지 않는 노선 구간 삭제시 예외를 던짐.")
    @Test
    void deleteSectionWithNotExistLine() {
        // given
        when(lineRepository.findById(eq(LINE_ID)))
                .thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> sectionService.deleteSection(LINE_ID, STATION_ID))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("존재하지 않는 지하철 역 삭제시 예외를 던짐.")
    @Test
    void deleteSectionWithNotExistStation() {
        // given
        when(lineRepository.findById(eq(LINE_ID)))
                .thenReturn(Optional.of(line));

        // when
        assertThatThrownBy(() -> sectionService.deleteSection(LINE_ID, NOT_EXIST_STATION_ID))
                .isInstanceOf(StationNotFoundException.class);
    }
}
