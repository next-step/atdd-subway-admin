package nextstep.subway.station;

import nextstep.subway.domain.Station;
import nextstep.subway.exception.ValueFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationTest {
    @DisplayName("역 생성시 역확인")
    @Test
    public void 역생성시_역확인() {
        //when
        Station station = Station.create("미사역");

        //then
        assertThat(station.name()).isEqualTo("미사역");
    }

    @DisplayName("역 생성시 유효성 검사")
    @Test
    public void 역생성시_유효성검사() {
        //when
        //then
        assertThatThrownBy(() -> Station.create("")).isInstanceOf(ValueFormatException.class);
        assertThatThrownBy(() -> Station.create(null)).isInstanceOf(ValueFormatException.class);
    }
}
