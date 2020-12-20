package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.StationFixtures;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LineStationDomainServiceTest {
    private LineStationDomainService lineStationDomainService;

    @Mock
    private StationService stationService;

    @Mock
    private LineRepository lineRepository;

    @BeforeEach
    void setup() {
        lineStationDomainService = new LineStationDomainService(stationService);
    }

    @DisplayName("Station 정보를 안전하게 Line 도메인에 속한 정보로 해석해서 가져올 수 있다.")
    @Test
    void getSafeStationInfoTest() {
        Long stationId = 1L;
        String stationName = "test";
        LocalDateTime now = LocalDateTime.now();
        given(stationService.getStation(stationId))
                .willReturn(StationFixtures.createStationFixture(stationId, stationName, now));

        SafeStationInfo safeStationInfo = lineStationDomainService.getStationSafely(stationId);

        assertThat(safeStationInfo).isNotNull();
        assertThat(safeStationInfo).isEqualTo(new SafeStationInfo(1L, stationName, now, null));
    }

    @DisplayName("존재하지 않는 Station 정보 요청 시 Line 도메인의 예외 발생")
    @Test
    void getSafeStationInfoFailTest() {
        Long notExistStationId = 44L;
        given(stationService.getStation(notExistStationId))
                .willThrow(new StationNotExistException("Station Aggregate Error"));

        assertThatThrownBy(() -> lineStationDomainService.getStationSafely(notExistStationId))
                .isInstanceOf(StationNotFoundException.class);
    }
}