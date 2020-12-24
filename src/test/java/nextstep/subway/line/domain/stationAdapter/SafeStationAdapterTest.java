package nextstep.subway.line.domain.stationAdapter;

import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationFixtures;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SafeStationAdapterTest {
    private SafeStationAdapter safeStationAdapter;

    @Mock
    private StationService stationService;

    @BeforeEach
    void setup() {
        safeStationAdapter = new SafeStationAdapter(stationService);
    }

    @DisplayName("Station 정보를 안전하게 Line 도메인에 속한 정보로 해석해서 가져올 수 있다.")
    @Test
    void getSafeStationInfoTest() {
        Long stationId = 1L;
        String stationName = "갱남역";
        given(stationService.getStation(stationId)).willReturn(StationFixtures.ID1_STATION);

        SafeStationInfo safeStationInfo = safeStationAdapter.getStationSafely(stationId);

        assertThat(safeStationInfo).isNotNull();
        assertThat(safeStationInfo.getId()).isEqualTo(stationId);
        assertThat(safeStationInfo.getName()).isEqualTo(stationName);
    }

    @DisplayName("존재하지 않는 Station 정보 요청 시 Line 도메인의 예외 발생")
    @Test
    void getSafeStationInfoFailTest() {
        Long notExistStationId = 44L;
        given(stationService.getStation(notExistStationId))
                .willThrow(new StationNotExistException("Station Aggregate Error"));

        assertThatThrownBy(() -> safeStationAdapter.getStationSafely(notExistStationId))
                .isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("Station ID 목록으로 Station 정보를 ID 오름차순으로 정렬해서 안전하게 받아올 수 있다.")
    @Test
    void getStationsSafely() {
        // given
        Long station1Id = 1L;
        Long station2Id = 2L;
        Long station3Id = 3L;
        int expectedSize = 3;
        List<Station> mockStations = Arrays.asList(
                StationFixtures.ID3_STATION,
                StationFixtures.ID1_STATION,
                StationFixtures.ID2_STATION
        );
        List<Long> stationIds = Arrays.asList(station3Id, station2Id, station1Id);

        given(stationService.getStations(stationIds)).willReturn(mockStations);

        // when
        List<SafeStationInfo> safeStationInfos = safeStationAdapter.getStationsSafely(stationIds);

        // then
        assertThat(safeStationInfos).hasSize(expectedSize);
        assertThat(safeStationInfos.get(0).getId()).isEqualTo(station1Id);
        assertThat(safeStationInfos.get(2).getId()).isEqualTo(station3Id);
    }
}