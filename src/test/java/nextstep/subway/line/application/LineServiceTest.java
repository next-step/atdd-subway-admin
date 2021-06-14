package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;

/**
 * LineService 클래스 기능 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService service;

    @Test
    @DisplayName("구간이 포함된 신규노선 저장")
    void save_line() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue", 1L, 2L, 10);
        Line line = lineRequest.toLine();
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(upStation, downStation, 10, line);
        given(stationRepository.findById(1L)).willReturn(Optional.of(upStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(downStation));
        given(lineRepository.save(any(Line.class))).willReturn(line);

        // when
        LineResponse lineResponse = service.saveLine(lineRequest);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.toLineResponse().getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.toLineResponse().getColor()),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("모든 노선 조회")
    void find_all_lines() {
        // given
        Line blueLine = new Section(new Station("사당역"), new Station("서울역"), 10, new Line("4호선", "blue")).getLine();
        Line greenLine = new Section(new Station("강남역"), new Station("역삼역"), 10, new Line("2호선", "green")).getLine();
        List<Line> lines = Arrays.asList(blueLine, greenLine);
        given(lineRepository.findAll()).willReturn(lines);

        // when
        List<LineResponse> lineResponses = service.findAllLines();

        // then
        List<String> lineNames = Arrays.asList("4호선", "2호선");
        assertThat(lineResponses).extracting("name").containsAll(lineNames);
    }

    @Test
    @DisplayName("ID로 노선 찾기")
    void find_line_by_id() {
        // given
        Line line = new Line("4호선", "green");
        Section greenSection = new Section(new Station("강남역"), new Station("역삼역"), 10, line);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        LineResponse lineResponse = service.findLineById(1L);

        // then
        assertThat(lineResponse.getName()).isEqualTo(line.toLineResponse().getName());
    }

    @Test
    @DisplayName("노선 정보 수정")
    void line_info_update() {
        // given
        Line line = new Line("2호선", "green");
        Section greenSection = new Section(new Station("강남역"), new Station("역삼역"), 10, line);
        LineRequest updateLineRequest = new LineRequest("2호선", "green", 3L, 4L, 4);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        service.updateLine(1L, updateLineRequest);
        LineResponse lineResponse = service.findLineById(1L);

        // then
        assertThat(lineResponse.getName()).isEqualTo(updateLineRequest.getName());
    }

    @Test
    @DisplayName("ID기준 노선 삭제")
    void delete_line_by_id() {
        // given
        Line line = new Line("2호선", "green");
        Section greenSection = new Section(new Station("강남역"), new Station("역삼역"), 10, line);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        service.deleteLineById(1L);

        // then
        verify(lineRepository).delete(line);
        verify(sectionRepository).deleteAllByLineId(anyLong());
    }

    @Test
    @DisplayName("중복등록 예외처리")
    void duplicate_key_exception() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue", 1L, 2L, 3);
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(upStation, downStation, 10, lineRequest.toLine());
        given(stationRepository.findById(1L)).willReturn(Optional.of(upStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(downStation));
        given(lineRepository.save(any(Line.class))).willThrow(DataIntegrityViolationException.class);

        // when
        assertThatThrownBy(() -> service.saveLine(lineRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("노선 생성에 실패했습니다. 이미 존재하는 노선입니다.");
    }

    @Test
    @DisplayName("노선 조회 실패 예외처리")
    void findLine_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(lineRepository.findById(1L)).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.findLineById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선 수정 시도 예외처리")
    void update_line_info_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        LineRequest updateLineRequest = new LineRequest("2호선", "green");
        given(lineRepository.findById(1L)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> service.updateLine(1L, updateLineRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("수정 대상 노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수정하려는 노선이름이 이미 존재할 경우 예외처리")
    void update_line_info_by_name_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
        given(lineRepository.findByName(anyString())).willReturn(Optional.of(line));

        // when
        assertThatThrownBy(() -> service.updateLine(1L, new LineRequest("1호선", "green")))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("동일한 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선 삭제 시도 예외처리")
    void delete_line_by_id_exception() {
        // given
        Line line = new Line("1호선", "blue");
        given(lineRepository.findById(1L)).willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.deleteLineById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("삭제 대상 노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("기존 노선에 신규구간 추가")
    void createNewSection_toLine() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("교대역");
        Station station3 = new Station("서초역");
        Station station4 = new Station("방배역");
        Station station5 = new Station("사당역");
        // 기존에 저장된 노선과 구간들
        Line greenLine = new Line("2호선", "green");
        new Section(station1, station2, 3, greenLine);
        Sections sections = greenLine.createSections();
        sections.addSection(new Section(station2, station3, 3, greenLine));
        sections.addSection(new Section(station3, station4, 3, greenLine));

        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 7);
        LineResponse line = greenLine.toLineResponse();

        given(lineRepository.findById(anyLong())).willReturn(Optional.of(greenLine));
        given(stationRepository.findById(1L)).willReturn(Optional.of(station1));
        given(stationRepository.findById(2L)).willReturn(Optional.of(station5));

        LineResponse lineResponse = service.appendNewSectionToLine(1L, sectionRequest);

        // then
        List<String> resultStationNames = lineResponse.getStations()
                .stream()
                .map(res -> res.getName())
                .collect(Collectors.toList());
        List<String> comparisonTargetStationNames = Arrays.asList(station1, station2, station3, station5, station4)
                .stream()
                .map(station -> station.toStationResponse().getName())
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor()),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(5),
                () -> assertThat(Arrays.equals(resultStationNames.toArray(), comparisonTargetStationNames.toArray()))
                        .isTrue()
        );
    }

    @Test
    @DisplayName("구간 삭제 성공")
    void delete_section() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("교대역");
        Station station3 = new Station("서초역");
        Station station4 = new Station("방배역");
        // 기존에 저장된 노선과 구간들
        Line greenLine = new Line("2호선", "green");
        new Section(station1, station2, 3, greenLine);
        Sections sections = greenLine.createSections();
        sections.addSection(new Section(station2, station3, 3, greenLine));
        sections.addSection(new Section(station3, station4, 3, greenLine));

        given(lineRepository.findById(anyLong())).willReturn(Optional.ofNullable(greenLine));
        given(stationRepository.findById(anyLong())).willReturn(Optional.ofNullable(station2));

        service.removeSectionByStationId(1L, 2L);

        // then
        assertThat(greenLine.createSections().getSections().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("노선기준 구간 목록이 등록되지 않은 경우 예외처리")
    void delete_section_exception1() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // then
        assertThatThrownBy(() -> service.removeSectionByStationId(1L, 2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("조회된 역이 등록되지 않은 경우 예외처리")
    void delete_section_exception2() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("교대역");
        Station station3 = new Station("서초역");
        Station station4 = new Station("방배역");
        // 기존에 저장된 노선과 구간들
        Line greenLine = new Line("2호선", "green");
        new Section(station1, station2, 3, greenLine);
        new Section(station2, station3, 3, greenLine);
        new Section(station3, station4, 3, greenLine);

        given(lineRepository.findById(anyLong())).willReturn(Optional.ofNullable(greenLine));
        given(stationRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // then
        assertThatThrownBy(() -> service.removeSectionByStationId(1L, 2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록된 역이 아닙니다. 역 ID : 2");
    }

    @Test
    @DisplayName("노선 구간에 포함된 역이 아닌 경우 예외처리")
    void delete_section_exception3() {
        // given
        Station station1 = new Station("강남역");
        Station station2 = new Station("교대역");
        Station station3 = new Station("서초역");
        Station station4 = new Station("방배역");
        Station station5 = new Station("사당역");
        // 기존에 저장된 노선과 구간들
        Line greenLine = new Line("2호선", "green");
        new Section(station1, station2, 3, greenLine);
        new Section(station2, station3, 3, greenLine);
        new Section(station3, station4, 3, greenLine);

        given(lineRepository.findById(anyLong())).willReturn(Optional.ofNullable(greenLine));
        given(stationRepository.findById(anyLong())).willReturn(Optional.ofNullable(station5));

        // then
        assertThatThrownBy(() -> service.removeSectionByStationId(1L, 2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("구간에 포함된 역이 아닙니다.");
    }
}
