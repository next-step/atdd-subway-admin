package nextstep.subway.line.domain;

import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.StationFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
}