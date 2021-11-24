package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StationTest {

    @DisplayName("지하철역이 잘 만들어지는지 확인")
    @ParameterizedTest
    @ValueSource(strings = { "강남역", "서울대입구역" })
    void 지하철역_생성(String name) {
        // when
        Station station = Station.from(name);
        
        // then
        assertThat(station.getName()).isEqualTo(name);
    }

    @DisplayName("지하철역이 수정되는지 확인")
    @ParameterizedTest
    @CsvSource(value = { "강남역:교대역", "서울대입구역:낙성대역" }, delimiter = ':')
    void 지하철역_수정(String name, String updatedName) {
        // given
        Station station = Station.from(name);
        
        // when
        station.update(Station.from(updatedName));
        
        // then
        assertThat(station.getName()).isEqualTo(updatedName);
    }

}
