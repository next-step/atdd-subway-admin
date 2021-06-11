package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationCommandServiceTest {
    private StationCommandService stationCommandService;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationQueryUseCase stationQueryUseCase;

    private StationRequest stationRequest;
    private Station station;

    @BeforeEach
    void setUp() {
        stationCommandService = new StationCommandService(stationRepository, stationQueryUseCase);
        stationRequest = new StationRequest("서울역");
        station = stationRequest.toStation();
    }

    @DisplayName("요청한 역을 저장한다.")
    @Test
    void saveStation() {
        //given
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        //when
        StationResponse actual = stationCommandService.saveStation(stationRequest);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }

    @DisplayName("요청한 역을 제거한다.")
    @Test
    void deleteStationById() {
        //given
        when(stationQueryUseCase.findById(anyLong())).thenReturn(station);

        //when
        stationCommandService.deleteStationById(anyLong());

        //then
        verify(stationRepository).delete(station);
    }
}