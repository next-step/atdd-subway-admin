package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("지하철 구간")
class SectionTest {

    @Test
    @DisplayName("구간 거리가 0 이하인 경우 실패한다.")
    void validateDistance() {
        //given
        Station upStation = Station.from("강남");
        Station downStation = Station.from("광교");

        //when //then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Line.of("신분당선", "bg-red-600", upStation, downStation, 0));
    }
}
