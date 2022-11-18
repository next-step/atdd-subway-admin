package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("요청한 구간을 연결하는 클래스 테스트")
class SectionConnectManagerTest {

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Line line;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        line = new Line("2호선", "bg-green-600");
    }

    @Test
    void 역과_역_사이에_새로운_구간을_추가한다() {
        line.addSection(new Section(교대역, 역삼역, 10));

        SectionConnectManager.connectAll(line, new Section(교대역, 강남역, 7));

        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void 맨_처음_구간을_추가한다() {
        line.addSection(new Section(강남역, 역삼역, 10));

        SectionConnectManager.connectAll(line, new Section(교대역, 강남역, 10));

        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    void 맨_마지막_구간을_추가한다() {
        line.addSection(new Section(교대역, 강남역, 10));

        SectionConnectManager.connectAll(line, new Section(강남역, 역삼역, 10));

        assertThat(line.getSections()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(ints = { 10, 20, 30 })
    void 거리가_추가하려는_구간에_지정된_거리보다_크거나_같으면_구간을_추가할_수_없다(int addDistance) {
        line.addSection(new Section(교대역, 역삼역, 10));

        assertThatThrownBy(() -> {
            SectionConnectManager.connectAll(line, new Section(교대역, 강남역, addDistance));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.INVALID_DISTANCE.getMessage());
    }
}
