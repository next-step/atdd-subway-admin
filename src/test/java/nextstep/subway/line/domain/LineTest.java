package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class LineTest {
    
    @Test
    @DisplayName("노선이 잘 만들어지는지 확인")
    void 노선_생성() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        
        // when
        Line line = Line.of("2호선", "파란색", upStation, downStation, 15);
        
        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("2호선"),
                () -> assertThat(line.getColor()).isEqualTo("파란색")
                );
    }
    
    @Test
    @DisplayName("노선이 수정되는지 확인")
    void 노선_수정() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 15);
        Line updateLine = Line.of("분당선", "노란색", upStation, downStation, 15);
        
        // when
        line.update(updateLine);
        
        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("분당선"),
                () -> assertThat(line.getColor()).isEqualTo("노란색")
                );
    }

}
