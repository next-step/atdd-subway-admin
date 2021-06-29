package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private static final Long 등록되지_않은_노선_ID = 0L;
    private static final Long 등록되지_않은_역_ID = 0L;

    private static final Long 지하철_1호선_ID = 1L;
    private static final Long 지하철_2호선_ID = 1L;

    private static final Long 신도림역_ID = 1L;
    private static final Long 서울역_ID = 2L;
    private static final Long 강남역_ID = 3L;
    private static final Long 역삼역_ID = 4L;
    private static final Long 사당역_ID = 5L;

    private static final int 거리 = 10;

    private static final Station 신도림역 = new Station(신도림역_ID, "신도림역");
    private static final Station 서울역 = new Station(서울역_ID, "서울역");
    private static final Station 강남역 = new Station(강남역_ID, "강남역");
    private static final Station 역삼역 = new Station(역삼역_ID, "역삼역");
    private static final Station 사당역 = new Station(사당역_ID, "사당역");

    private static final Line 지하철_1호선 = new Line(지하철_1호선_ID, "1호선", "남색", 신도림역, 서울역, 거리);
    private static final Line 지하철_2호선 = new Line(지하철_2호선_ID, "2호선", "녹색", 강남역, 역삼역, 거리);
    private static final Line 지하철_2호선_수정_후 = new Line(지하철_2호선_ID, "3호선", "주황색", 강남역, 역삼역, 거리);

    private static final LineRequest 지하철_1호선_생성_정보 = new LineRequest("1호선", "남색", 신도림역_ID, 서울역_ID, 거리);
    private static final LineRequest 지하철_3호선_수정_정보 = new LineRequest("3호선", "주황색", null, null, 0);

    private static final LineResponse 지하철_1호선_반환_정보 = LineResponse.of(지하철_1호선);

    @DisplayName("노선을 생성한다")
    @Test
    void saveLine() {
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);
        when(lineRepository.save(any(Line.class))).thenReturn(지하철_1호선);

        assertThat(lineService.saveLine(지하철_1호선_생성_정보).getName()).isEqualTo(지하철_1호선_반환_정보.getName());
        assertThat(lineService.saveLine(지하철_1호선_생성_정보).getStations().size()).isEqualTo(
            지하철_1호선_반환_정보.getStations().size());
    }

    @DisplayName("이름이 없으면 노선을 생성을 실패한다")
    @Test
    void saveLine_EmptyName_ExceptionThrown() {
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);

        LineRequest 잘못된_노선_생성_정보1 = new LineRequest(null, "남색", 신도림역_ID, 서울역_ID, 거리);
        LineRequest 잘못된_노선_생성_정보2 = new LineRequest("", "남색", 신도림역_ID, 서울역_ID, 거리);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보1));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보2));
    }

    @DisplayName("색깔이 없으면 노선을 생성을 실패한다")
    @Test
    void saveLine_EmptyColor_ExceptionThrown() {
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);

        LineRequest 잘못된_노선_생성_정보1 = new LineRequest("1호선", null, 신도림역_ID, 서울역_ID, 거리);
        LineRequest 잘못된_노선_생성_정보2 = new LineRequest("1호선", "", 신도림역_ID, 서울역_ID, 거리);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보1));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보2));
    }

    @DisplayName("역 정보가 없을 때 노선을 생성을 실패한다")
    @Test
    void saveLine_NotFoundStation_ExceptionThrown() {
        when(stationService.findStationById(등록되지_않은_역_ID)).thenThrow(EntityNotFoundException.class);

        LineRequest 잘못된_노선_생성_정보 = new LineRequest("1호선", "남색", 등록되지_않은_역_ID, 서울역_ID, 거리);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보));
    }

    @DisplayName("상행역과 하행역이 같아 노선을 생성을 실패한다")
    @Test
    void saveLine_SameStations_ExceptionThrown() {
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);

        LineRequest 잘못된_노선_생성_정보 = new LineRequest("1호선", "남색", 신도림역_ID, 신도림역_ID, 거리);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보));
    }

    @DisplayName("거리가 1보다 작아 노선을 생성을 실패한다")
    @Test
    void saveLine_TooSmall_ExceptionThrown() {
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);

        LineRequest 잘못된_노선_생성_정보 = new LineRequest("1호선", "남색", 신도림역_ID, 서울역_ID, 0);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> lineService.saveLine(잘못된_노선_생성_정보));
    }

    @DisplayName("모든 노선 정보를 반환한다")
    @Test
    void findAllLines() {
        when(lineRepository.findAll()).thenReturn(Collections.singletonList(지하철_1호선));

        assertThat(lineService.findAllLines().get(0).getName()).isEqualTo(지하철_1호선_반환_정보.getName());
    }

    @DisplayName("노선 ID로 노선 정보를 반환한다")
    @Test
    void findLineById() {
        when(lineRepository.findById(지하철_1호선_ID)).thenReturn(Optional.of(지하철_1호선));

        assertThat(lineService.findLineById(지하철_1호선_ID).getName()).isSameAs(지하철_1호선_반환_정보.getName());
    }

    @DisplayName("노선 ID로 노선 정보를 불러온다")
    @Test
    void findLineByLineId() {
        when(lineRepository.findById(지하철_1호선_ID)).thenReturn(Optional.of(지하철_1호선));

        assertThat(lineService.findLineByLineId(지하철_1호선_ID)).isSameAs(지하철_1호선);
    }

    @DisplayName("등록되지 않은 노선은 불러올 수 없다")
    @Test
    void findLineByLineId_NotExist_ExceptionThrown() {
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
            lineService.findLineByLineId(등록되지_않은_노선_ID)
        );
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void updateLine() {
        when(lineRepository.findById(지하철_2호선_ID)).thenReturn(Optional.of(지하철_2호선));
        when(lineRepository.saveAndFlush(지하철_2호선)).thenReturn(지하철_2호선_수정_후);

        LineResponse 지하철_2호선_수정결과 = lineService.updateLine(지하철_2호선_ID, 지하철_3호선_수정_정보);

        assertThat(지하철_2호선_수정결과.getName()).isEqualTo("3호선");
        assertThat(지하철_2호선_수정결과.getColor()).isEqualTo("주황색");
    }

    @DisplayName("등록되지 않은 노선은 수정할 수 없다")
    @Test
    void updateLine_NotFound_ExceptionThrown() {
        when(lineRepository.findById(등록되지_않은_노선_ID)).thenThrow(EntityNotFoundException.class);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
            lineService.updateLine(등록되지_않은_노선_ID, 지하철_3호선_수정_정보)
        );
    }

    @DisplayName("노선을 삭제한다")
    @Test
    void deleteLineById() {
        when(lineRepository.findById(지하철_1호선_ID)).thenReturn(Optional.of(지하철_1호선));

        lineService.deleteLineById(지하철_1호선_ID);

        verify(lineRepository).delete(지하철_1호선);
    }

    @DisplayName("등록되지 않은 노선은 삭제할 수 없다")
    @Test
    void deleteLineById_NotFound_ExceptionThrown() {
        when(lineRepository.findById(등록되지_않은_노선_ID)).thenThrow(EntityNotFoundException.class);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
            lineService.deleteLineById(등록되지_않은_노선_ID)
        );
    }

    @DisplayName("상행역의 앞에 추가")
    @Test
    void addSection_beforeUpStation() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(사당역_ID, 신도림역_ID, 3);
        Section 구간 = new Section(지하철, 사당역, 신도림역, 3);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);
        when(sectionRepository.save(any(Section.class))).thenReturn(구간);

        LineResponse lineResponse = lineService.addSection(지하철_ID, 구간_생성_정보);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(사당역_ID, 신도림역_ID, 서울역_ID);
    }

    @DisplayName("상행역의 뒤에 추가")
    @Test
    void addSection_afterUpStation() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(신도림역_ID, 사당역_ID, 3);
        Section 구간 = new Section(지하철, 신도림역, 사당역, 3);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);
        when(sectionRepository.save(any(Section.class))).thenReturn(구간);

        LineResponse lineResponse = lineService.addSection(지하철_ID, 구간_생성_정보);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(신도림역_ID, 사당역_ID, 서울역_ID);
    }

    @DisplayName("하행역의 앞에 추가")
    @Test
    void addSection_beforeDownStation() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(사당역_ID, 서울역_ID, 3);
        Section 구간 = new Section(지하철, 사당역, 서울역, 3);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);
        when(sectionRepository.save(any(Section.class))).thenReturn(구간);

        LineResponse lineResponse = lineService.addSection(지하철_ID, 구간_생성_정보);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(신도림역_ID, 사당역_ID, 서울역_ID);
    }

    @DisplayName("하행역의 뒤에 추가")
    @Test
    void addSection_afterDownStation() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(서울역_ID, 사당역_ID, 3);
        Section 구간 = new Section(지하철, 서울역, 사당역, 3);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);
        when(sectionRepository.save(any(Section.class))).thenReturn(구간);

        LineResponse lineResponse = lineService.addSection(지하철_ID, 구간_생성_정보);

        List<Long> stationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(신도림역_ID, 서울역_ID, 사당역_ID);
    }

    @DisplayName("역 사이 추가: 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSection_TooLongDistance_ExceptionThrown() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(사당역_ID, 서울역_ID, 거리);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            lineService.addSection(지하철_ID, 구간_생성_정보));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection_AlreadyAddedStations_ExceptionThrown() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(신도림역_ID, 서울역_ID, 거리);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(서울역_ID)).thenReturn(서울역);
        when(stationService.findStationById(신도림역_ID)).thenReturn(신도림역);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            lineService.addSection(지하철_ID, 구간_생성_정보));
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_NotFoundStation_ExceptionThrown() {
        Long 지하철_ID = 10L;
        Line 지하철 = new Line(지하철_ID, "1호선", "남색", 신도림역, 서울역, 거리);
        SectionRequest 구간_생성_정보 = new SectionRequest(역삼역_ID, 사당역_ID, 거리);
        when(lineRepository.findById(지하철_ID)).thenReturn(Optional.of(지하철));
        when(stationService.findStationById(역삼역_ID)).thenReturn(역삼역);
        when(stationService.findStationById(사당역_ID)).thenReturn(사당역);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            lineService.addSection(지하철_ID, 구간_생성_정보));
    }

}
