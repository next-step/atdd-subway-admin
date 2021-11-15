package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import nextstep.subway.common.domain.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 역")
class StationTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Station.from(Name.from("강남")));
    }

    @Test
    @DisplayName("이름이 비어있는 상태로 객체화하면 IllegalArgumentException")
    void instance_nullName_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Station.from(null))
            .withMessageEndingWith("'name' must not be null");
    }

}
