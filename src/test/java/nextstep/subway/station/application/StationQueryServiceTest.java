package nextstep.subway.station.application;

import nextstep.subway.station.application.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationQueryServiceTest {
    private StationQueryService stationQueryService;

    @Mock
    private StationRepository stationRepository;
    private Station station1;
    private Station station2;

    @BeforeEach
    void setUp() {
        stationQueryService = new StationQueryService(stationRepository);
        station1 = new Station("용산역");
        station2 = new Station("서울역");
    }

    @DisplayName("요청한 ID에 맞는 역을 리턴한다.")
    @Test
    void findById() {
        //given
        when(stationRepository.findById(anyLong())).thenReturn(Optional.of(station1));

        //when
        Station actual = stationQueryService.findById(anyLong());

        //then
        assertThat(actual.getName()).isEqualTo("용산역");
    }

    @DisplayName("요청한 ID에 맞는 역이 없다면 예외를 발생시킨다.")
    @Test
    void findByIdException() {
        //given
        when(stationRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> stationQueryService.findById(anyLong()))
                .isInstanceOf(StationNotFoundException.class); //then
    }

    @DisplayName("모든 역을 리턴한다.")
    @Test
    void findAllStations() {
        //given
        when(stationRepository.findAll()).thenReturn(Arrays.asList(station1, station2));

        //when
        List<StationResponse> actual = stationQueryService.findAllStations();

        //then
        assertThat(actual).containsAll(Arrays.asList(StationResponse.of(station1), StationResponse.of(station2)));
    }
}