package nextstep.subway.application;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("지하철역 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @DisplayName("지하철역 등록")
    @Test
    void saveStation() {
        String stationName = "강남역";
        StationRequest stationRequest = new StationRequest(stationName);

        when(stationRepository.save(any(Station.class))).then(AdditionalAnswers.returnsFirstArg());
        StationResponse stationResponse = stationService.saveStation(stationRequest);
        assertThat(stationResponse.getName()).isEqualTo(stationName);
    }

    @DisplayName("등록된 모든 지하철역 조회")
    @Test
    void findAllStations() {
        List<Station> stations = Arrays.asList(
                new Station("강남역"),
                new Station("양재역"),
                new Station("신사역")
        );
        when(stationRepository.findAll()).thenReturn(stations);
        List<StationResponse> stationResponses = stationService.findAllStations();
        assertThat(stationResponses.size()).isEqualTo(stations.size());
    }
}