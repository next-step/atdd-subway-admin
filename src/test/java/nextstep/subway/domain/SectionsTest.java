package nextstep.subway.domain;

import nextstep.subway.exception.CannotAddSectionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    Station 상행종점역 = new Station("합정역");
    Station 하행종점역 = new Station("신촌역");
    int 거리 = 10;

    @Test
    void 상행역_기준_구간_추가() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 4;

        노선.addSection(상행종점역, 홍대역, 구간_거리);

        assertThat(노선.getLineStations())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 홍대역, 4),
                        new Section(노선, 홍대역, 하행종점역, 6)
                        ));

    }

    @Test
    void 하행역_기준_구간_추가() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 4;

        노선.addSection(홍대역, 하행종점역, 구간_거리);

        assertThat(노선.getLineStations())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 홍대역, 6),
                        new Section(노선, 홍대역, 하행종점역, 4)
                ));
    }

    @Test
    void 하행종점역_기준_구간_추가() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 11;

        노선.addSection(홍대역, 상행종점역, 구간_거리);

        assertThat(노선.getLineStations())
                .isEqualTo(new Sections(
                        new Section(노선, 홍대역, 상행종점역, 11),
                        new Section(노선, 상행종점역, 하행종점역, 10)
                ));
    }

    @Test
    void 상행종점역_기준_구간_추가() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 11;

        노선.addSection(하행종점역, 홍대역, 구간_거리);

        assertThat(노선.getLineStations())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 하행종점역, 10),
                        new Section(노선, 하행종점역, 홍대역, 11)
                ));
    }

    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 거리+1;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 하행종점역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
        assertThatThrownBy(() -> 노선.addSection(상행종점역, 홍대역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 등록하려는_구간_거리는_0이_될_수_없다() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        Station 홍대역 = new Station("홍대역");
        int 구간_거리 = 0;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 하행종점역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
        assertThatThrownBy(() -> 노선.addSection(상행종점역, 홍대역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없다() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);

        int 구간_거리 = 5;

        assertThatThrownBy(() -> 노선.addSection(상행종점역, 하행종점역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없다() {
        Line 노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 거리);
        Station 홍대역 = new Station("홍대역");
        Station 가양역 = new Station("가양역");
        int 구간_거리 = 5;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 가양역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }
}