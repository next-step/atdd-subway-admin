package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.Constants.SECOND_LINE_COLOR;
import static nextstep.subway.common.Constants.SECOND_LINE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void getStations_확인() {
        //given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");

        //when
        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 잠실역, 10));
        line.getSections().addSection(new Section(강남역, 역삼역, 5));

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 잠실역);
    }

    @DisplayName("Line_역 사이에 새로운 역을 등록할 경우")
    @Test
    void Line_addSection_성공케이스_역사이에_새로운역_등록() {
        //given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 잠실역, 10));

        //when
        line.getSections().addSection(new Section(강남역, 역삼역, 5));
//        line.addSectionOld(new Section(강남역, 역삼역, 5));

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 잠실역);
        /*assertThat(line.getSections().stream()
                .filter(section -> section.getUpStation().getName().equals("강남역"))
                .findFirst().get().getDistance()).isEqualTo(5);*/
        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }
}