package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Line line1 = Line.of("2호선", "초록색", upStation1, downStation1, 15);
        
        Station upStation2 = Station.from("강남역");
        Station downStation2 = Station.from("양재역");
        Line line2 = Line.of("신분당선", "빨간색", upStation2, downStation2, 20);
        
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.add(Section.of(line1, upStation1, downStation1, 15));
        sectionList.add(Section.of(line2, upStation2, downStation2, 20));
        
        // when
        Sections sections = Sections.from(sectionList);
        
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
        
        Sections sections = Sections.from(new ArrayList<>(Arrays.asList(Section.of(line, upStation, downStation, 15))));
        
        // when
        Station newUpStation = Station.from("서울대입구역");
        Station newDownStation = Station.from("강남역");
        Line newLine = Line.of("2호선", "초록색", newUpStation, newDownStation, 50);
        sections.add(Section.of(newLine, newUpStation, newDownStation, 50));
        
        // then
        assertThat(sections.count()).isEqualTo(2);
    }
}
