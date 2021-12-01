package nextstep.subway.line;

import nextstep.subway.Exception.CannotUpdateSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    private Station 강남역;
    private Station 양재역;
    private Station 광교역;
    private Section 신분당선_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        광교역 = new Station("광교역");
        신분당선_구간 = new Line("신분당선", "bg-red-600", 강남역, 광교역, 10).getSections().get(0);
    }

    @DisplayName("지하철역이 기존 역 사이에 들어가면 거리가 줄어드는지 확인")
    @Test
    void updateUpStation() {
        //when
        신분당선_구간.updateUpStation(양재역, 7);

        //then
        assertThat(신분당선_구간.getDistance()).isEqualTo(3);
    }

    @DisplayName("기존 길이보다 길이가 같거나 길면 추가할 수 없음")
    @Test
    void updateUpStation_Exception() {
        assertThatThrownBy(() -> {
            신분당선_구간.updateUpStation(양재역, 10);
        }).isInstanceOf(CannotUpdateSectionException.class).hasMessage("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
    }
}
