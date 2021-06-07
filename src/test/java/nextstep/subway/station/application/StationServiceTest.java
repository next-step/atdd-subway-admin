package nextstep.subway.station.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

/**
 * StationService 클래스 기능 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
class StationServiceTest {
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    @DisplayName("지하철 역 일괄 등록")
    void create_station_list() {
        // given
        List<StationRequest> stationRequests = Arrays.asList(new StationRequest("강남역"),
                new StationRequest("역삼역"));
        List<Station> stations = stationRequests.stream()
                .map(req -> req.toStation())
                .collect(Collectors.toList());
        when(this.stationRepository.saveAll(any(List.class))).thenReturn(stations);

        // when
        List<StationResponse> stationResponses = this.stationService.saveAllStations(stationRequests);

        // then
        List<String> requestNames = stationRequests.stream()
                .map(req -> req.getName())
                .collect(Collectors.toList());
        List<String> responseNames = stationResponses.stream()
                .map(res -> res.getName())
                .collect(Collectors.toList());
        assertThat(responseNames).containsAll(requestNames);
    }

    @Test
    @DisplayName("중복된 지하철 역 일괄 등록 예외")
    void create_station_list_duplicate_exception() {
        // given
        List<StationRequest> stationRequests = Arrays.asList(new StationRequest("강남역"),
                new StationRequest("강남역"));
        when(this.stationRepository.saveAll(any(List.class))).thenThrow(DataIntegrityViolationException.class);

        // when
        assertThatThrownBy(() -> this.stationService.saveAllStations(stationRequests))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
