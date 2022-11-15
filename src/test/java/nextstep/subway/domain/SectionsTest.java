package nextstep.subway.domain;

import nextstep.subway.exception.CannotAddSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    Station 하행종점역;
    Station 상행종점역;

    Line 노선;

    @BeforeEach
    void setUp() {
        상행종점역 = new Station("충정로");
        하행종점역 = new Station("사당");
        노선 = new Line("2호선", "blue", 상행종점역, 하행종점역, 10);

    }

    @Test
    void 상행역_기준_새로운_구간을_역_사이에_추가할_수_있다() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(상행종점역, 홍대입구, 4);

        Station 당산 = new Station("당산");
        노선.addSection(홍대입구, 당산, 4);

        assertThat(노선.getStation())
                .isEqualTo(new Stations(상행종점역, 홍대입구, 당산, 하행종점역));
    }

    @Test
    void 하행역_기준_새로운_구간을_역_사이에_추가할_수_있다() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(홍대입구, 하행종점역, 4);

        Station 당산 = new Station("당산");
        노선.addSection(당산, 홍대입구, 4);

        assertThat(노선.getSections().getStations())
                .isEqualTo(new Stations(
                        상행종점역,
                        당산,
                        홍대입구,
                        하행종점역));
    }

    @Test
    void 하행종점역_기준_구간_추가() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(하행종점역, 홍대입구, 4);

        assertThat(노선.getStation())
                .isEqualTo(new Stations(
                        상행종점역,
                        하행종점역,
                        홍대입구));
    }
    @Test
    void 상행종점역_기준_구간_추가() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(홍대입구, 상행종점역, 4);

        assertThat(노선.getSections().getStations())
                .isEqualTo(new Stations(
                        홍대입구,
                        상행종점역,
                        하행종점역));
    }

    @Test
    void 구간_조회시_순서가_정렬됨() {
        Sections sections = new Sections(
                new Section(노선, new Station("st2"), new Station("st3"), 10),
                new Section(노선, new Station("st1"), new Station("st2"), 10),
                new Section(노선, new Station("st3"), new Station("st4"), 10)
        );

        assertThat(sections.getStations())
                .isEqualTo(new Stations(
                            new Station("st1"),
                            new Station("st2"),
                            new Station("st3"),
                            new Station("st4")));

    }

    @Test
    void 상행역과_하행역이_이미_노선에_등록되어_있다면_추가할_수_없음() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(홍대입구, 하행종점역, 4);

        assertThatThrownBy(() -> 노선.addSection(상행종점역, 하행종점역, 1))
                .isInstanceOf(CannotAddSectionException.class);

    }
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        Station 홍대입구 = new Station("홍대입구");
        노선.addSection(홍대입구, 하행종점역, 4);
        Station 합정 = new Station("합정");
        Station 당산 = new Station("당산");

        assertThatThrownBy(() -> 노선.addSection(합정, 당산, 1))
                .isInstanceOf(CannotAddSectionException.class);

    }

}