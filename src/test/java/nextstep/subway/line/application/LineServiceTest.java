package nextstep.subway.line.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 노선 서비스 관련 기능")
@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    LineRepository lineRepository;
    
    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    LineService lineService;

    @DisplayName("지하철 노선정보 목록을 조회")
    @Test
    void find_allLineInfos() {
        // given
        List<Line> sample = List.of(new Line("신분당선", "bg-red-600"), new Line("2호선", "bg-green-600"));
        
        when(lineRepository.findAll()).thenReturn(sample);

        // when
        List<LineInfoResponse> LineInfoResponses = lineService.findAllForLineInfo();

        // then
        Assertions.assertThat(LineInfoResponses).hasSize(sample.size());

        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.get(0).getName()).isEqualTo(sample.get(0).getName()),
            () -> Assertions.assertThat(LineInfoResponses.get(0).getColor()).isEqualTo(sample.get(0).getColor()),
            () -> Assertions.assertThat(LineInfoResponses.get(0).getStations()).isEmpty()
        );

        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.get(1).getName()).isEqualTo(sample.get(1).getName()),
            () -> Assertions.assertThat(LineInfoResponses.get(1).getColor()).isEqualTo(sample.get(1).getColor()),
            () -> Assertions.assertThat(LineInfoResponses.get(1).getStations()).isEmpty()
        );
    }

    @DisplayName("지하철 노선정보를 조회")
    @Test
    void find_lineInfo() {
        // given
        Line sample = new Line("신분당선", "bg-red-600");
        
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(sample));

        // when
        LineInfoResponse LineInfoResponses = lineService.findLineInfo(1L);

        // then
        assertAll(
            () -> Assertions.assertThat(LineInfoResponses.getName()).isEqualTo(sample.getName()),
            () -> Assertions.assertThat(LineInfoResponses.getColor()).isEqualTo(sample.getColor()),
            () -> Assertions.assertThat(LineInfoResponses.getStations()).isEmpty()
        );
    }

    @DisplayName("지하철 노선정보를 변경")
    @Test
    void update_lineInfo() {
        // given
        Line sample = new Line("신분당선", "bg-red-600");
        
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(sample));

        // when
        lineService.updateLineInfo(1L, new Line("구분당선", "bg-blue-600"));

        // then
        LineInfoResponse lineResponse = lineService.findLineInfo(1L);

        assertAll(
            () -> Assertions.assertThat(lineResponse.getName()).isEqualTo("구분당선"),
            () -> Assertions.assertThat(lineResponse.getColor()).isEqualTo("bg-blue-600")
        );
    }

    @DisplayName("지하철 노선정보를 삭제")
    @Test
    void delete_line() {
        // given

        // when
        lineService.deleteLineInfo(1L);

        // then
        verify(lineRepository, times(1)).deleteById(anyLong());
    }

    @DisplayName("구간정보를 포함한 지하철 노선정보를 생성")
    @Test
    void create_lineWithSection() {
        // given
        Line expectedLine = new Line("3호선", "bg-orange-600");

        Section section = Section.valueOf(new Station("대화"), new Station("수서"), Distance.valueOf(100));
        section.addSectionAtLine(expectedLine);

        when(lineRepository.save(any(Line.class))).thenReturn(expectedLine);
        
        // when
        Line line = lineService.saveLine(expectedLine);

        // then
        Assertions.assertThat(line).isEqualTo(expectedLine);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void saveSection() {
        // given
        Station upStation = new Station("대화");
        Station downStation = new Station("수서");
        Distance distance = Distance.valueOf(200);
        Section section = Section.valueOf(upStation, downStation, distance);

        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        // when
        Section savedSection = lineService.saveSection(section);

        // then
        assertAll("validateValue",
            () -> Assertions.assertThat(savedSection.getUpStation()).isEqualTo(upStation),
            () -> Assertions.assertThat(savedSection.getDownStation()).isEqualTo(downStation),
            () -> Assertions.assertThat(savedSection.getDistance()).isEqualTo(distance)
        );
    }
}
