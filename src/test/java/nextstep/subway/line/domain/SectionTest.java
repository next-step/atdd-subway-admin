package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
                () -> assertThat(section.getDistance()).isEqualTo(Distance.from(15)),
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
        assertAll(
                () -> assertThat(section.equals(comparedSection)).isFalse(),
                () -> assertThat(section.hashCode() == comparedSection.hashCode()).isFalse()
                );
    }
    

    
    @DisplayName("같은 상행역인지 확인")
    @ParameterizedTest
    @CsvSource(value = { "서울대입구역:true", "봉천역:false" }, delimiter = ':')
    void 같은_상핵역_확인(String stationName, boolean expected) {
        // given
        Station upStation = Station.from("서울대입구역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 30);
        Section section = Section.of(line, upStation, downStation, 30);
        
        Station newUpStation = Station.from(stationName);
        
        // when, then
        assertThat(section.isSameUpStation(newUpStation)).isEqualTo(expected);
    }
    
    @DisplayName("같은 하행역인지 확인")
    @ParameterizedTest
    @CsvSource(value = { "교대역:true", "봉천역:false" }, delimiter = ':')
    void 같은_하행역_확인(String stationName, boolean expected) {
        // given
        Station upStation = Station.from("서울대입구역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 30);
        Section section = Section.of(line, upStation, downStation, 30);
        
        Station newDownStation = Station.from(stationName);
        
        // when, then
        assertThat(section.isSameDownStation(newDownStation)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("기존 구간이 잘변경되는지 확인 (상행역)")
    void 기존_구간_변경_확인_상행역() {
        // given
        Station upStation = Station.from("서울대입구역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 30);
        Section section = Section.of(line, upStation, downStation, 30);

        Station newUpStation = Station.from("사당역");
        
        // when
        section.moveUpStationTo(newUpStation, Distance.from(10));
        
        // then
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(newUpStation),
                () -> assertThat(section.getDistance()).isEqualTo(Distance.from(20))
                );
    }
    
    @Test
    @DisplayName("기존 구간이 잘변경되는지 확인 (하행역)")
    void 기존_구간_변경_확인_하행역() {
        // given
        Station upStation = Station.from("서울대입구역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "파란색", upStation, downStation, 30);
        Section section = Section.of(line, upStation, downStation, 30);

        Station newDownStation = Station.from("낙성대입구역");
        
        // when
        section.moveDownStationTo(newDownStation, Distance.from(20));
        
        // then
        assertAll(
                () -> assertThat(section.getDownStation()).isEqualTo(newDownStation),
                () -> assertThat(section.getDistance()).isEqualTo(Distance.from(10))
                );
    }

}
