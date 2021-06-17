package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationQueryUseCase;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionCommandServiceTest {
    private SectionCommandService sectionCommandService;

    @Mock
    private LineQueryUseCase lineQueryUseCase;

    @Mock
    private StationQueryUseCase stationQueryUseCase;

    @Mock
    private SectionRepository sectionRepository;

    private Line line1;
    private SectionRequest sectionRequest1;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        sectionCommandService = new SectionCommandService(lineQueryUseCase, stationQueryUseCase, sectionRepository);
        line1 = new Line("1호선", "blue");
        ReflectionTestUtils.setField(line1, "id", 1L);
        sectionRequest1 = new SectionRequest(1L, 2L, 10);
        upStation = new Station("용산역");
        ReflectionTestUtils.setField(upStation, "id", 1L);
        downStation = new Station("서울역");
        ReflectionTestUtils.setField(downStation, "id", 2L);
    }

    @DisplayName("요청한 노선에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        when(lineQueryUseCase.findById(line1.getId())).thenReturn(line1);
        when(stationQueryUseCase.findById(sectionRequest1.getUpStationId())).thenReturn(upStation);
        when(stationQueryUseCase.findById(sectionRequest1.getDownStationId())).thenReturn(downStation);

        //when
        sectionCommandService.addSection(line1.getId(), sectionRequest1);

        //then
        assertThat(line1.getStations()).contains(upStation, downStation);
    }
}