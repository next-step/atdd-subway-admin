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
        Line line = Line.of("2호선", "초록색", upStation1, downStation1, Distance.from(15));
        
        Station upStation2 = Station.from("사당역");
        Station downStation2 = Station.from("강남역");
        
        Section section1 = Section.of(line, upStation1, downStation1, Distance.from(15));
        Section section2 = Section.of(line, upStation2, downStation2, Distance.from(20));
        
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
        Line line = Line.of("2호선", "초록색", upStation, downStation, Distance.from(15));
        
        Sections sections = line.getSections();
        
        // when
        Station newUpStation = Station.from("서울대입구역");
        Station newDownStation = Station.from("강남역");
        sections.add(Section.of(line, newUpStation, newDownStation, Distance.from(50)));
        
        // then
        assertThat(sections.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("지하철역 목록을 순서대로 잘 조회하는지 확인")
    void 지하철역_목록_조회() {
        // given
        Station secondUpStation = Station.from("봉천역");
        Station secondDownStation = Station.from("서울대입구역");
        Line line = Line.of("5호선", "초록색", secondUpStation, secondDownStation, Distance.from(50));
        
        Sections sections = line.getSections();
        
        Station thirdUpStation = Station.from("서울대입구역");
        Station thirdDownStation = Station.from("낙성대역");
        Section thirdSection = Section.of(line, thirdUpStation, thirdDownStation, Distance.from(20));
        sections.add(thirdSection);
        
        Station firstUpStation = Station.from("신림역");
        Station firstDownStation = Station.from("봉천역");
        Section firstSection = Section.of(line, firstUpStation, firstDownStation, Distance.from(50));
        sections.add(firstSection);
        
        Station fourthUpStation = Station.from("낙성대역");
        Station fourthDownStation = Station.from("사당역");
        Section fourthSection = Section.of(line, fourthUpStation, fourthDownStation, Distance.from(50));
        sections.add(fourthSection);
        
        // when, then
        assertThat(sections.getStations()).containsExactly(firstUpStation, firstDownStation, secondDownStation, thirdDownStation, fourthDownStation);
    }
    
    @Test
    @DisplayName("구간의 지하철역이 제거되는지 확인")
    void 구간_제거_확인() {
        // given
        Station firstStation = Station.from("봉천역");
        Station secondStation = Station.from("서울대입구역");
        Line line = Line.of("2호선", "초록색", firstStation, secondStation, Distance.from(20));
        
        Sections sections = line.getSections();
        
        Station thirdStation = Station.from("낙성대역");
        Section newSection = Section.of(line, secondStation, thirdStation, Distance.from(30));
        sections.add(newSection);
        
        // when
        sections.remove(secondStation);
        
        // then
        assertAll(
                () -> assertThat(sections.count()).isEqualTo(1),
                () -> assertThat(sections.getStations().size()).isEqualTo(2),
                () -> assertThat(sections.getSectionAt(0).getDistance()).isEqualTo(Distance.from(50))
                );
    }
    
    @Test
    @DisplayName("구간의 첫번째 지하철역이 제거되는지 확인")
    void 첫번째역_제거_확인() {
        // given
        Station firstStation = Station.from("봉천역");
        Station secondStation = Station.from("서울대입구역");
        Line line = Line.of("2호선", "초록색", firstStation, secondStation, Distance.from(20));
        
        Sections sections = line.getSections();
        
        Station thirdStation = Station.from("낙성대역");
        Section newSection = Section.of(line, secondStation, thirdStation, Distance.from(30));
        sections.add(newSection);
        
        // when
        sections.remove(firstStation);
        
        // then
        assertAll(
                () -> assertThat(sections.count()).isEqualTo(1),
                () -> assertThat(sections.getStations().size()).isEqualTo(2),
                () -> assertThat(sections.getSectionAt(0).getDistance()).isEqualTo(Distance.from(30))
                );
    }
    
    @Test
    @DisplayName("구간의 마지막 지하철역이 제거되는지 확인")
    void 마지막역_제거_확인() {
        // given
        Station firstStation = Station.from("봉천역");
        Station secondStation = Station.from("서울대입구역");
        Line line = Line.of("2호선", "초록색", firstStation, secondStation, Distance.from(20));
        
        Sections sections = line.getSections();
        
        Station thirdStation = Station.from("낙성대역");
        Section newSection = Section.of(line, secondStation, thirdStation, Distance.from(30));
        sections.add(newSection);
        
        // when
        sections.remove(thirdStation);
        
        // then
        assertAll(
                () -> assertThat(sections.count()).isEqualTo(1),
                () -> assertThat(sections.getStations().size()).isEqualTo(2),
                () -> assertThat(sections.getSectionAt(0).getDistance()).isEqualTo(Distance.from(20))
                );
    }
    
}
