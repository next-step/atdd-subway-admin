package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.line.CreateLineRequest;
import nextstep.subway.dto.line.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("지하철 노선 생성")
    public void createLine() {
        // Given
        final CreateLineRequest createLineRequest = new CreateLineRequest("name", "color", 1L, 2L, 10);
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("판교역");
        given(stationRepository.findById(1L)).willReturn(Optional.of(upStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(downStation));
        given(lineRepository.save(any(Line.class))).will(AdditionalAnswers.returnsFirstArg());

        // When
        LineResponse lineResponse = lineService.saveLine(createLineRequest);

        // Then
        assertAll(
            () -> assertThat(lineResponse.getName()).isEqualTo(createLineRequest.getName()),
            () -> assertThat(lineResponse.getColor()).isEqualTo(createLineRequest.getColor()),
            () -> assertThat(lineResponse.getStations())
                .extracting("name")
                .containsOnly(upStation.getName(), downStation.getName())
        );
        verify(stationRepository, times(2)).findById(anyLong());
        verify(lineRepository).save(any(Line.class));
    }
}
