package nextstep.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 도메인 테스트")
class StationTest {

    @DisplayName("Station이 아닌 Entity정보 응답시 포함할 Station 정보 생성")
    @Test
    void toMapForOpen() {
        Station station = new Station("강남역");
        Map<String, Object> map = station.toMapForOpen();
        assertAll(
                () -> assertThat(map.size()).isEqualTo(4),
                () -> assertThat(map.containsKey("id")).isTrue(),
                () -> assertThat(map.containsKey("name")).isTrue()
        );
    }
}