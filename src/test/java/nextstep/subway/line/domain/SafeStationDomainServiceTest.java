package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationFixtures;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SafeStationDomainServiceTest {
    private SafeStationDomainService safeStationDomainService;

    @Mock
    private StationService stationService;

    @Mock
    private LineRepository lineRepository;

    @BeforeEach
    void setup() {
        safeStationDomainService = new SafeStationDomainService(stationService);
    }

    @DisplayName("Station 정보를 안전하게 Line 도메인에 속한 정보로 해석해서 가져올 수 있다.")
    @Test
    void getSafeStationInfoTest() {
        Long stationId = 1L;
        String stationName = "test";
        LocalDateTime now = LocalDateTime.now();
        given(stationService.getStation(stationId))
                .willReturn(StationFixtures.createStationFixture(stationId, stationName, now));

        SafeStationInfo safeStationInfo = safeStationDomainService.getStationSafely(stationId);

        assertThat(safeStationInfo).isNotNull();
        assertThat(safeStationInfo).isEqualTo(new SafeStationInfo(1L, stationName, now, null));
    }

    @DisplayName("존재하지 않는 Station 정보 요청 시 Line 도메인의 예외 발생")
    @Test
    void getSafeStationInfoFailTest() {
        Long notExistStationId = 44L;
        given(stationService.getStation(notExistStationId))
                .willThrow(new StationNotExistException("Station Aggregate Error"));

        assertThatThrownBy(() -> safeStationDomainService.getStationSafely(notExistStationId))
                .isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("Station ID 목록으로 Station 정보를 안전하게 받아올 수 있다.")
    @ParameterizedTest
    @MethodSource("getStationsSafelyResource")
    void getStationsSafely(List<Station> mockStations, int expectedSize) {
        Long station1Id = 1L;
        Long station2Id = 2L;
        List<Long> stationIds = Arrays.asList(station1Id, station2Id);

        given(stationService.getStations(stationIds)).willReturn(mockStations);

        List<SafeStationInfo> safeStationInfos = safeStationDomainService.getStationsSafely(stationIds);

        assertThat(safeStationInfos).hasSize(expectedSize);
    }
    public static Stream<Arguments> getStationsSafelyResource() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                StationFixtures.createStationFixture(1L, "test", LocalDateTime.now()),
                                StationFixtures.createStationFixture(2L, "test2", LocalDateTime.now())
                        ),
                        2
                ),
                Arguments.of(new ArrayList<>(), 0)
        );
    }
}