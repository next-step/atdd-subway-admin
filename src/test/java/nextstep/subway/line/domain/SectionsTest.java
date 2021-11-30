package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class SectionsTest {

    @Test
    @DisplayName("구간 목록이 잘 만들어지는지 확인")
    void 구간_목록_생성() {
        // given
        Station upStation1 = Station.from("강남역");
        Station downStation1 = Station.from("교대역");
        Line line = Line.of("2호선", "초록색", upStation1, downStation1, 15);
        
        Station upStation2 = Station.from("사당역");
        Station downStation2 = Station.from("강남역");
        
        Section section1 = Section.of(line, upStation1, downStation1, 15);
        Section section2 = Section.of(line, upStation2, downStation2, 20);
        
        // when
        Sections sections = Sections.from(section1, section2);
        
        // then
        assertThat(sections.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("구간 목록에 새 구간이 추가되는지 확인")
    void 구간_목록_새_구간_추가() {
        // given
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("교대역");
        Line line = Line.of("2호선", "초록색", upStation, downStation, 15);
        
        Sections sections = line.getSections();
        
        // when
        Station newUpStation = Station.from("서울대입구역");
        Station newDownStation = Station.from("강남역");
        sections.add(Section.of(line, newUpStation, newDownStation, 50));
        
        // then
        assertThat(sections.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("구간 목록의 맨 앞에 구간이 추가되는지 확인")
    void 구간_목록_맨_앞_구간_추가() {
        // given
        Station upStation = Station.from("서울대입구역");
        Station downStation = Station.from("낙성대역");
        Line line = Line.of("2호선", "초록색", upStation, downStation, 15);
        
        Sections sections = line.getSections();
        
        // when
        Station firstUpStation = Station.from("봉천역");
        Station firstDownStation = Station.from("서울대입구역");
        Section firstSection = Section.of(line, firstUpStation, firstDownStation, 50);
        sections.add(firstSection);
        
        // then
        assertThat(sections.getStationAt(0)).isEqualTo(firstUpStation);
    }
    
    @Test
    @DisplayName("구간 목록의 맨 뒤에 구간이 추가되는지 확인")
    void 구간_목록_맨_뒤_구간_추가() {
        // given
        Station upStation = Station.from("봉천역");
        Station downStation = Station.from("서울대입구역");
        Line line = Line.of("2호선", "초록색", upStation, downStation, 15);
        
        Sections sections = line.getSections();
        
        // when
        Station lastUpStation = Station.from("서울대입구역");
        Station lastDownStation = Station.from("낙성대역");
        Section lastSection = Section.of(line, lastUpStation, lastDownStation, 50);
        sections.add(lastSection);
        
        // then
        assertThat(sections.getSectionAt(sections.count()-1)).isEqualTo(lastSection);
    }
    
    @Test
    @DisplayName("구간이 잘 정렬되는지 확인")
    void 구간_목록_정렬_확인() {
        // given
        Station secondUpStation = Station.from("봉천역");
        Station secondDownStation = Station.from("서울대입구역");
        Line line = Line.of("5호선", "초록색", secondUpStation, secondDownStation, 50);
        
        Sections sections = line.getSections();
        
        // when
        Station thirdUpStation = Station.from("서울대입구역");
        Station thirdDownStation = Station.from("낙성대역");
        Section thirdSection = Section.of(line, thirdUpStation, thirdDownStation, 20);
        sections.add(thirdSection);
        
        Station firstUpStation = Station.from("신림역");
        Station firstDownStation = Station.from("봉천역");
        Section firstSection = Section.of(line, firstUpStation, firstDownStation, 50);
        sections.add(firstSection);
        
        Station fourthUpStation = Station.from("낙성대역");
        Station fourthDownStation = Station.from("사당역");
        Section fourthSection = Section.of(line, fourthUpStation, fourthDownStation, 50);
        sections.add(fourthSection);
        
        // then
        assertAll(
                () -> assertThat(sections.getSectionAt(2)).isEqualTo(thirdSection),
                () -> assertThat(sections.getSectionAt(3)).isEqualTo(fourthSection)
                );
    }
    
}
