package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    @DisplayName("노선 생성")
    @Test
    public void 노선생성시_생성확인() {
        //when
        Line line = Line.create("신분당선", "red");

        //then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선 수정")
    @Test
    public void 노선수정시_수정확인() {
        //given
        Line line = Line.create("신분당선", "red");

        //when
        line.change("구분당선", "blue");

        //then
        assertThat(line.getName()).isEqualTo("구분당선");
        assertThat(line.getColor()).isEqualTo("blue");
    }

    @DisplayName("노선 생성시 구간 생성 확인")
    @Test
    public void 노선생성시_구간생성확인() {
        //given
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");

        //when
        Line line = Line.create("5호선", "purple", upStation, downStation, 10);

        //then
        assertThat(line.sectionsSize()).isEqualTo(1);
    }
}
