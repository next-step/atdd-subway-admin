package nextstep.subway.station.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    private static final Long 신도림역_ID = 1L;
    private static final Long 서울역_ID = 2L;
    private static final Long 등록되지_않은_역_ID = 0L;
    private static final Station 신도림역 = new Station(신도림역_ID, "신도림역");
    private static final Station 서울역 = new Station(서울역_ID, "서울역");
    private static final StationRequest 신도림역_생성_정보 = new StationRequest("신도림역");

    @DisplayName("역을 생성한다")
    @Test
    void saveLine() {
        when(stationRepository.save(any(Station.class))).thenReturn(신도림역);

        assertThat(stationService.saveStation(신도림역_생성_정보).getName()).isEqualTo("신도림역");
    }

    @DisplayName("이름이 없으면 역을 생성을 실패한다")
    @Test
    void saveLine_EmptyString_ExceptionThrown() {
        StationRequest 잘못된_역_생성_정보1 = new StationRequest(null);
        StationRequest 잘못된_역_생성_정보2 = new StationRequest("");

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> stationService.saveStation(잘못된_역_생성_정보1));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> stationService.saveStation(잘못된_역_생성_정보2));
    }

    @DisplayName("모든 역 정보를 반환한다")
    @Test
    void findAllStations() {
        when(stationRepository.findAll()).thenReturn(Arrays.asList(신도림역, 서울역));

        assertThat(stationService.findAllStations().size()).isEqualTo(2);
    }

    @DisplayName("역 ID로 역 정보를 불러온다")
    @Test
    void findStationById() {
        when(stationRepository.findById(신도림역_ID)).thenReturn(Optional.of(신도림역));

        assertThat(stationService.findStationById(신도림역_ID)).isEqualTo(신도림역);
    }

    @DisplayName("등록되지 않은 역은 불러올 수 없다")
    @Test
    void findStationById_NotFound_ExceptionThrown() {
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(
            () -> stationService.findStationById(등록되지_않은_역_ID));
    }

    @DisplayName("역을 삭제한다")
    @Test
    void deleteStationById() {
        when(stationRepository.findById(신도림역_ID)).thenReturn(Optional.of(신도림역));

        stationService.deleteStationById(신도림역_ID);

        verify(stationRepository).delete(신도림역);
    }

    @DisplayName("등록되지 않은 역은 삭제할 수 없다")
    @Test
    void deleteStationById_NotFound_ExceptionThrown() {
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(
            () -> stationService.deleteStationById(등록되지_않은_역_ID));
    }

}
