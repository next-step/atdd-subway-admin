package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private Line line;
    private Station addedStation;
    private Station downStation;
    private Station upStation;

    @BeforeEach
    public void setUp() {
        upStation = new Station("상행");
        downStation = new Station("하행");
        addedStation = new Station( "새로운 지하철역");
        line = new Line("1호선", "bg-blue-300", 10, upStation, downStation);
    }

    @Test
    public void 상행_구간_연장() {
        //when
        line.addSection(null, addedStation, 10);

        //then
        LineStation ascEnd = line.getAscEnd();
        assertThat(ascEnd.getDistance()).isEqualTo(0);
        assertThat(ascEnd.getName()).isEqualTo("새로운 지하철역");

        LineStation descEnd = line.getDescEnd();
        assertThat(descEnd.getDistance()).isEqualTo(20);
        assertThat(descEnd.getName()).isEqualTo("하행");

        assertThat(line.getLineStations().stream().map(LineStation::getName).collect(Collectors.toList())).containsExactly("새로운 지하철역", "상행", "하행");
    }

    @Test
    public void 하행_구간_연장() {
        //when
        line.addSection(downStation, addedStation, 10);

        //then
        LineStation ascEnd = line.getAscEnd();
        assertThat(ascEnd.getDistance()).isEqualTo(0);
        assertThat(ascEnd.getName()).isEqualTo("상행");

        LineStation descEnd = line.getDescEnd();
        assertThat(descEnd.getDistance()).isEqualTo(20);
        assertThat(descEnd.getName()).isEqualTo("새로운 지하철역");

        assertThat(line.getLineStations().stream().map(LineStation::getName).collect(Collectors.toList())).containsExactly("상행", "하행", "새로운 지하철역");
    }

    @Test
    public void 상행_하행_사이_구간_추가() {
        //when
        line.addSection(upStation, addedStation, 5);

        //then
        LineStation ascEnd = line.getAscEnd();
        assertThat(ascEnd.getDistance()).isEqualTo(0);
        assertThat(ascEnd.getName()).isEqualTo("상행");

        LineStation descEnd = line.getDescEnd();
        assertThat(descEnd.getDistance()).isEqualTo(10);
        assertThat(descEnd.getName()).isEqualTo("하행");

        assertThat(line.getLineStations().stream().map(LineStation::getName).collect(Collectors.toList())).containsExactly("상행", "새로운 지하철역", "하행");

        //when
        line.addSection(addedStation, new Station("새로추가된 역에 또 하나 더 추가함"), 2);

        //then
        assertThat(line.getLineStations().stream().map(LineStation::getName).collect(Collectors.toList())).containsExactly("상행", "새로운 지하철역", "새로추가된 역에 또 하나 더 추가함", "하행");

        //when
        line.addSection(upStation, new Station( "더 추가하기"), 4);

        //then
        assertThat(line.getLineStations().stream().map(LineStation::getName).collect(Collectors.toList())).containsExactly("상행", "더 추가하기", "새로운 지하철역", "새로추가된 역에 또 하나 더 추가함", "하행");
    }

    @Test
    public void 사이_구간_추가시_범위_벗어나는_경우_구간_추가가_불가능하다() {
        //then
        assertThatThrownBy(() -> line.addSection(upStation, addedStation, 10)).isInstanceOf(IllegalArgumentException.class);
    }
}
