package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionType;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    private static final Integer TEST_DISTANCE = 10;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private LineService lineService;

    private Line line;

    @BeforeEach
    void setUp() {
        Station startStation = new Station("시작");
        Station endStation = new Station("끝");
        Section 시작 = Section.of(new Distance(TEST_DISTANCE), SectionType.UP, startStation, endStation);
        Section 끝 = Section.fromDownStation(endStation);
        line = new Line("1호선", "blue");
        line.addSections(Arrays.asList(시작, 끝));
    }

    @Test
    @DisplayName("라인 한건 조회")
    void findById() {
        Mockito.when(lineRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.of(line));

        LineResponse byId = lineService.findLine(1L);

        assertAll(() -> {
            assertThat(byId.getName()).isEqualTo(line.getName());
            assertThat(byId.getColor()).isEqualTo(line.getColor());
        });
    }

    @Test
    @DisplayName("라인 한건 조회시 없을 경우 NotFoundException")
    void findByIdNotFoundException() {
        Mockito.when(lineRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.findLine(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("라인 목록 조회")
    void findAll() {
        Mockito.when(lineRepository.findAll())
            .thenReturn(Arrays.asList(line));
        List<LineResponse> lineResponseList = lineService.findLines();
        assertThat(lineResponseList.size()).isEqualTo(1);
    }

}