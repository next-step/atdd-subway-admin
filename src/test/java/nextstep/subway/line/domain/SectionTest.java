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
    
    @Test
    @DisplayName("같은 구간인지 확인")
    void 같은_구간_구별() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 15);
        
        Section section = Section.of(line, upStation, downStation, 15);
        Section comparedSection = Section.of(line, upStation, downStation, 15);
        
        // when, then
        // then
        assertAll(
                () -> assertThat(section.equals(comparedSection)).isTrue(),
                () -> assertThat(section.hashCode() == comparedSection.hashCode()).isTrue()
                );
    }
    
    @Test
    @DisplayName("다른 구간인지 확인")
    void 다른_구간_구별() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 15);
        Section section = Section.of(line, upStation, downStation, 15);
        
        Line comparedLine = Line.of("3호선", "빨간색", upStation, downStation, 17);
        Section comparedSection = Section.of(comparedLine, upStation, downStation, 17);
        
        // when, then
        // then
        assertAll(
                () -> assertThat(section.equals(comparedSection)).isFalse(),
                () -> assertThat(section.hashCode() == comparedSection.hashCode()).isFalse()
                );
    }

}
