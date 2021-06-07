package nextstep.subway.section.application;

import jdk.nashorn.internal.ir.annotations.Ignore;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
// 나는 왜 서비스를 테스트하지 못하는가?

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private SectionService sectionService;

    private static final long ANY_LINE_ID = 1L;
    private Line line;
    private Station upStationId;
    private Station downStationId;

    @BeforeEach
    void setUp() {
        upStationId = new Station(1L, "강남");
        downStationId = new Station(2L, "광교");
        line = new Line(1L,"신분당역", "빨강", Sections.of(new ArrayList<>()));
    }

    @Test
    @Ignore
    void name() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        given(stationRepository.findById(1L)).willReturn(Optional.of(upStationId));
        given(stationRepository.findById(2L)).willReturn(Optional.of(downStationId));
        // new Section 을 만들어야 해서 테스트를 못한다. Domain 으로 접근할 수 있을것같은데?
        sectionService.addSection(1L, new SectionRequest(1L, 2L, 10));
//        assertThat(line.getStations()).containsExactly(upStationId, downStationId);

//
//        given(lineRepository.findById(ANY_LINE_ID)).willReturn(Optional.of(line));
//
//        given(line.getSections()).willReturn(Sections.of(new ArrayList<>()));
//        line.getSections().getValues().add(new Section());
//        SectionRequest request = new SectionRequest(1L, 2L, 30);
//        final Line line = sut.addSection(ANY_LINE_ID, request);
//
//        assertThat(line.getStations()).containsExactly();
    }
}
