package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class SectionTest {
    
    @Test
    @DisplayName("구간이 잘 만들어지는지 확인")
    void 구간_생성() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 15);
        
        // when
        Section section = Section.of(line, upStation, downStation, 15);
        
        // then
        assertAll(
                () -> assertThat(section.getDistance()).isEqualTo(15),
                () -> assertThat(section.getUpStation().equals(upStation)).isTrue(),
                () -> assertThat(section.getDownStation().equals(downStation)).isTrue()
                );
    }

}
