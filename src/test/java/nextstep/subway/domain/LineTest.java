package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    @Test
    @DisplayName("라인의 이름과 색상정보를 변경한다")
    void change() {
        Station station2 = new Station(2L, "력삼역");
        Station station3 = new Station(3L, "선릉역");
        Line line = new Line(1L, "2호선", "bg-100", station2, station3, 100);
        line.change("100호선", "bg-gray");
        assertThat(line.getName()).isEqualTo("100호선");
        assertThat(line.getColor()).isEqualTo("bg-gray");
    }
}
