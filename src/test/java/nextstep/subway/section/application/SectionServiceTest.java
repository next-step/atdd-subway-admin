package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    private SectionService sectionService;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private SectionRepository sectionRepository;

    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(stationRepository, sectionRepository);
        line = new Line("2호선", "green");
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
    }

    @DisplayName("구간을 생성요청한다")
    @Test
    void createSectionWithValidLine() {
        // given
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(upStation));
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(downStation));
        SectionDto sectionDto = new SectionDto(line, 1L, 2L, 10);

        // when
        sectionService.createSection(sectionDto);

        // then
        verify(sectionRepository).save(any());
    }
}
