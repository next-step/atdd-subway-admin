package nextstep.subway.line.domain;

import nextstep.subway.exception.CanNotRemoveStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.Constants.SECOND_LINE_COLOR;
import static nextstep.subway.common.Constants.SECOND_LINE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("LineTest_Line_역 사이에 새로운 역을 등록할 경우")
    @Test
    void Line_addSection_성공케이스_역사이에_새로운역_등록() {
        //given
        Station 강남역 = new Station("강남역");
        Station 잠실역 = new Station("잠실역");
        Station 역삼역 = new Station("역삼역");
        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 잠실역, 10));

        //when
        line.getSections().addSection(new Section(강남역, 역삼역, 5));

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 잠실역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_새로운역을_상행종점으로_등록할 경우")
    @Test
    void Line_addSection_성공케이스_새로운역을_상행종점으로_등록() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));

        //when
        line.getSections().addSection(new Section(강남역, 역삼역, 5));

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 잠실역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_새로운역을_하행종점으로_등록할 경우")
    @Test
    void Line_addSection_성공케이스_새로운역을_하행종점으로_등록() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when
        line.getSections().addSection(new Section(역삼역, 잠실역, 5));

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 잠실역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_예외케이스_역사이에_등록시_새로운역구간이_더큰경우")
    @Test
    void Line_addSection_예외케이스_새로운역구간이_기존구간사이보다큼() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when-then
        assertThatThrownBy(() ->line.getSections().addSection(new Section(강남역, 잠실역, 10)))
                .isInstanceOf(RuntimeException.class);
        assertThat(line.getStations()).containsExactly(강남역, 역삼역);
        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });

    }

    @DisplayName("LineTest_예외케이스_상행역_하행역_모두_이미노선에_등록된경우")
    @Test
    void Line_addSection_예외케이스_상행역하행역모두_이미노선에등록됨() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when-then
        assertThatThrownBy(() ->line.getSections().addSection(new Section(강남역, 역삼역, 5)))
                .isInstanceOf(RuntimeException.class);
        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_예외케이스_상행역_하행역_모두_기존구간에_포함되어있지않은_경우")
    @Test
    void Line_addSection_예외케이스_상행역하행역모두_기존구간에_없는경우() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");
        Station 잠실나루역 = new Station("잠실나루역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when-then
        assertThatThrownBy(() ->line.getSections().addSection(new Section(잠실역, 잠실나루역, 5)))
                .isInstanceOf(RuntimeException.class);

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_성공케이스_중간역제거_경우")
    @Test
    void Line_removeStation_중간역제거_성공() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));
        line.getSections().addSection(new Section(역삼역, 잠실역, 5));

        //when
        line.getSections().removeStation(역삼역);

        //then
        assertThat(line.getStations()).containsExactly(강남역, 잠실역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(10);
        });
    }

    @DisplayName("LineTest_성공케이스_상행역제거_경우")
    @Test
    void Line_removeStation_상행역제거_성공() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));
        line.getSections().addSection(new Section(역삼역, 잠실역, 5));

        //when
        line.getSections().removeStation(강남역);

        //then
        assertThat(line.getStations()).containsExactly(역삼역, 잠실역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("역삼역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_성공케이스_하행역제거_경우")
    @Test
    void Line_removeStation_하행역제거_성공() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));
        line.getSections().addSection(new Section(역삼역, 잠실역, 5));

        //when
        line.getSections().removeStation(잠실역);

        //then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역);

        line.getSections().forEach(section -> {
            if (section.getUpStation().getName().equals("강남역"))
                assertThat(section.getDistance()).isEqualTo(5);
        });
    }

    @DisplayName("LineTest_예외케이스_없는역을_삭제하는_경우")
    @Test
    void Line_addSection_예외케이스_없는역을_삭제하는_경우() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 잠실역 = new Station("잠실역");
        Station 잠실나루역 = new Station("잠실나루역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));
        line.getSections().addSection(new Section(역삼역, 잠실역, 5));

        //when-then
        assertThatThrownBy(() ->line.getSections().removeStation(잠실나루역))
                .isInstanceOf(CanNotRemoveStationException.class);
    }

    @DisplayName("LineTest_예외케이스_구간하나일때_삭제하는_경우")
    @Test
    void Line_addSection_예외케이스_구간하나일때_삭제하는_경우() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Line line = new Line(SECOND_LINE_NAME,SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when-then
        assertThatThrownBy(() ->line.getSections().removeStation(강남역))
                .isInstanceOf(CanNotRemoveStationException.class);
    }
}