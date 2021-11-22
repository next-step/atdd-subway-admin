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
import nextstep.subway.line.domain.SectionList;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
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
class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private LineService lineService;

    private Line line;
    private SectionList sectionList;

    @BeforeEach
    public void setUp() {
        Section 시작 = new Section(10, 1, new Station("시작"), SectionType.UP);
        Section 끝 = new Section(0, 2, new Station("끝"), SectionType.DOWN);
        line = new Line("1호선", "blue");
        시작.setLine(line);
        끝.setLine(line);
    }

    @Test
    @DisplayName("라인 한건 조회")
    public void findById() {
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
    public void findByIdNotFoundException() {
        Mockito.when(lineRepository.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.findLine(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("라인 목록 조회")
    public void findAll() {
        Mockito.when(lineRepository.findAll())
            .thenReturn(Arrays.asList(line));
        List<LineResponse> lineResponseList = lineService.findLines();
        assertThat(lineResponseList.size()).isEqualTo(1);
    }

}