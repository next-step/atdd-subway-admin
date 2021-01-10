package nextstep.subway.line.application;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("노선 관련 서비스")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    private LineService lineService;

    Station 삼성역 = new Station("삼성역");
    Station 잠실역 = new Station("잠실역");
    Station 잠실새내역 = new Station("잠실새내역");
    Station 잠실나루역 = new Station("잠실나루역");
    Line _2호선 = new Line("2호선", "bg-green-200", 삼성역, 잠실역, 1000L);

    @BeforeEach
    void beforeEach() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("`Line` 생성 후, 등록된 `Station` 목록 반환")
    @Test
    void createLine() {
        // Given
        when(lineRepository.save(any())).thenReturn(_2호선);
        when(stationService.findById(any())).thenReturn(삼성역).thenReturn(잠실역);
        // When
        LineResponse lineResponse = lineService.saveLine(new LineRequest());
        // Then
        assertThat(lineResponse.getStations())
                .extracting("name")
                .containsExactly("삼성역", "잠실역");
    }

    @DisplayName("`Line`에 `Section` 추가")
    @Test
    void addSectionInLine() {
        // When
        addLineStation(_2호선, 삼성역, 잠실새내역);
        // Then
        assertThat(_2호선.getStations())
                .extracting(Station::getName)
                .containsExactly("삼성역", "잠실새내역", "잠실역");
    }

    @DisplayName("`Section` 추가시 예외 확인 - 이미 등록된 구간")
    @Test
    void checkExceptionToAddSection() {
        // Given
        Line line = new Line("2호선", "bg-green-200", 삼성역, 잠실새내역, 1150);
        // When&Then
        assertThatThrownBy(() -> addLineStation(line, 삼성역, 잠실새내역))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 등록된 구간 입니다.");
    }

    @DisplayName("`Section` 추가시 예외 확인 - 등록이 불가능한 구간")
    @Test
    void checkExceptionToAddSection2() {
        // Given
        Line line = new Line("2호선", "bg-green-200", 삼성역, 잠실새내역, 1150);
        // When&Then
        assertThatThrownBy(() -> addLineStation(line, 잠실역, 잠실나루역))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("등록할 수 없는 구간 입니다.");
    }

    @DisplayName("`Line`에서 `Station` 삭제")
    @Test
    void deleteStationInLine() {
        // Given
        addLineStation(_2호선, 잠실새내역, 잠실역);
        // When
        removeLineStation(잠실새내역);
        // Then
        assertThat(_2호선.getStations())
                .extracting(Station::getName)
                .containsExactly("삼성역", "잠실역");
    }

    @DisplayName("`Line`에서 `Station` 삭제시 예외 확인 - 구간이 1개이면 삭제 불가")
    @Test
    void checkExceptionToDeleteStationInLine() {
        // Given
        when(lineRepository.findById(any())).thenReturn(Optional.of(_2호선));
        // When&Then
        assertThatThrownBy(() -> removeLineStation(잠실역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("구간이 1개이면 역을 삭제할 수 없습니다.");
    }

    private void removeLineStation(Station station) {
        when(stationService.findById(any())).thenReturn(station);
        lineService.removeStationInLine(1L, 1L);
    }

    private void addLineStation(Line line, Station upStation, Station downStation) {
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationService.findById(any())).thenReturn(upStation).thenReturn(downStation);
        lineService.addSection(1L, new SectionRequest());
    }
}
