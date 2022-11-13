package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("SectionService 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Mock
    private SectionRepository sectionRepository;

    private LineService lineService;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
        sectionService = new SectionService(lineService, sectionRepository);
    }

    @Test
    void 특정_노선의_전체_지하철_구간_목록_검색() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Line line = new Line("2호선", "bg-green-600");
        line.addSection(new Section(upStation, downStation, 10));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        when(sectionRepository.findByLine(line)).thenReturn(line.getSections());

        assertThat(sectionService.findByLine(1L)).hasSize(1);
    }
}
