package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    @BeforeEach
    void setup() {
        stationService = new StationService(stationRepository);
    }

    @DisplayName("특정 역의 정보를 가져올 수 있다")
    @Test
    void getStationTest() {
        Long stationId = 1L;
        String stationName = "갱남";
        Station mockStation = new Station(stationName);
        given(stationRepository.findById(stationId)).willReturn(Optional.of(mockStation));

        Station station = stationService.getStation(stationId);

        assertThat(station.getName()).isEqualTo(stationName);
    }

    @DisplayName("존재하지 않는 특정 역의 정보 조회 시도 시 예외 발생")
    @Test
    void getStationFailTest() {
        Long notExistId = 4L;

        assertThatThrownBy(() -> stationService.getStation(notExistId)).isInstanceOf(StationNotExistException.class);
    }
}