package nextstep.subway.domain;

import nextstep.subway.exception.CannotAddSectionException;
import nextstep.subway.exception.CannotRemoveSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    Line 노선;
    Station 상행종점역 = new Station("합정역");
    Station 하행종점역 = new Station("신촌역");
    Station 가양역 = new Station("가양역");
    Station 홍대역 = new Station("홍대역");
    int 종점간_거리 = 10;


    @BeforeEach
    void setup() {
        노선 = new Line("2호선", "color", 상행종점역, 하행종점역, 종점간_거리);
    }

    @Test
    void 상행역_기준_구간_추가() {
        int 구간_거리 = 종점간_거리 - 2;

        노선.addSection(상행종점역, 홍대역, 구간_거리);

        assertThat(노선.getSections())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 홍대역, 구간_거리),
                        new Section(노선, 홍대역, 하행종점역, 종점간_거리 - 구간_거리)
                ));

    }

    @Test
    void 하행역_기준_구간_추가() {
        int 구간_거리 = 종점간_거리 - 2;

        노선.addSection(홍대역, 하행종점역, 구간_거리);

        assertThat(노선.getSections())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 홍대역, 종점간_거리 - 구간_거리),
                        new Section(노선, 홍대역, 하행종점역, 구간_거리)
                ));
    }

    @Test
    void 상행종점역_기준_구간_추가() {
        int 구간_거리 = 종점간_거리;

        노선.addSection(홍대역, 상행종점역, 구간_거리);

        assertThat(노선.getSections())
                .isEqualTo(new Sections(
                        new Section(노선, 홍대역, 상행종점역, 구간_거리),
                        new Section(노선, 상행종점역, 하행종점역, 종점간_거리)
                ));
    }

    @Test
    void 하행종점역_기준_구간_추가() {
        int 구간_거리 = 종점간_거리;

        노선.addSection(하행종점역, 홍대역, 구간_거리);

        assertThat(노선.getSections())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 하행종점역, 종점간_거리),
                        new Section(노선, 하행종점역, 홍대역, 구간_거리)
                ));
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void 구간_거리_검증() {
        int 구간_거리 = 종점간_거리 + 1;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 하행종점역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
        assertThatThrownBy(() -> 노선.addSection(상행종점역, 홍대역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 등록하려는_구간_거리는_0이_될_수_없다() {
        int 구간_거리 = 0;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 하행종점역, 구간_거리))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 노선.addSection(상행종점역, 홍대역, 구간_거리))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없다() {
        int 구간_거리 = 5;

        assertThatThrownBy(() -> 노선.addSection(상행종점역, 하행종점역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없다() {
        int 구간_거리 = 5;

        assertThatThrownBy(() -> 노선.addSection(홍대역, 가양역, 구간_거리))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 가운데_역을_제거할_경우_재배치를_함() {
        노선.addSection(상행종점역, 가양역, 4);
        노선.addSection(가양역, 홍대역, 4);

        노선.removeSection(가양역);
        노선.removeSection(홍대역);

        assertThat(노선.getSections().getStations())
                .containsExactly(상행종점역, 하행종점역);
    }

    @Test
    void 하행종점역이_제거될_경우_다음역이_하행종점역이_됨() {
        노선.addSection(상행종점역, 가양역, 4);
        노선.addSection(가양역, 홍대역, 4);

        노선.removeSection(하행종점역);
        노선.removeSection(홍대역);

        assertThat(노선.getSections().getStations())
                .containsExactly(상행종점역, 가양역);
    }

    @Test
    void 상행종점역이_제거될_경우_다음역이_상행종점역이_됨() {
        노선.addSection(상행종점역, 가양역, 4);
        노선.addSection(가양역, 홍대역, 4);

        노선.removeSection(상행종점역);
        노선.removeSection(가양역);

        assertThat(노선.getSections().getStations())
                .containsExactly(홍대역, 하행종점역);
    }

    @Test
    void 가운데_역을_제거할_경우_거리는_두_구간의_거리의_합으로_정함() {
        노선.addSection(상행종점역, 가양역, 4);
        노선.addSection(가양역, 홍대역, 4);

        노선.removeSection(가양역);

        assertThat(노선.getSections())
                .isEqualTo(new Sections(
                        new Section(노선, 상행종점역, 홍대역, 8),
                        new Section(노선, 홍대역, 하행종점역, 2)));

    }

    @Test
    void 구간이_하나인_노선에서_역을_제거할_수_없음() {
        assertThatThrownBy(() -> 노선.removeSection(상행종점역))
                .isInstanceOf(CannotRemoveSectionException.class);

        assertThatThrownBy(() -> 노선.removeSection(하행종점역))
                .isInstanceOf(CannotRemoveSectionException.class);
    }

    @Test
    void 노선에_등록되어있지_않은_역을_제거할_수_없음() {
        assertThatThrownBy(() -> 노선.removeSection(가양역))
                .isInstanceOf(CannotRemoveSectionException.class);
    }
}