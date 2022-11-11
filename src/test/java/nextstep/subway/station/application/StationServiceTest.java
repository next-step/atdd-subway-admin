package nextstep.subway.station.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationExceptionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;


    @Test
    void 지하철역_저장() {
        Station station = new Station("교대역");
        StationRequest request = new StationRequest("교대역");

        when(stationRepository.save(request.toStation())).thenReturn(station);
        StationResponse actual = stationService.saveStation(request);

        assertEquals("교대역", actual.getName());
    }

    @Test
    void id로_지하철역_검색() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(new Station("강남역")));

        Station station = stationService.findById(1L);

        assertEquals("강남역", station.getName());
    }

    @Test
    void id로_지하철역_검색시_데이터가_존재하지_않으면_NotFoundException_발생() {
        assertThatThrownBy(() -> {
            stationService.findById(1L);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage(StationExceptionCode.NOT_FOUND_BY_ID.getMessage());
    }

    @Test
    void 전체_지하철역_목록_검색() {
        when(stationRepository.findAll())
                .thenReturn(Arrays.asList(new Station("강남역"), new Station("교대역")));

        List<StationResponse> responses = stationService.findAllStations();

        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.stream().map(StationResponse::getName))
                        .contains("강남역", "교대역")
        );
    }
}
