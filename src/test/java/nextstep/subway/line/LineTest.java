package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @DisplayName("종점역 조회시 종점역 확인")
    @Test
    public void 종점역조회시_종점역확인() throws Exception {
        //given
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");
        Line line = Line.create("5호선", "purple", upStation, downStation, 10);

        //when
        Station findUpStation = line.upEndStation();
        Station findDownStation = line.downEndStation();

        //then
        assertThat(findUpStation).isEqualTo(upStation);
        assertThat(findDownStation).isEqualTo(downStation);
    }

    @DisplayName("노선의 역목록 조회시 상행에서 하행으로 정렬 확인")
    @Test
    public void 노선의역목록조회시_상행에서하행으로정렬확인() throws Exception {
        //given
        Station upStation = Station.create("미사역");
        Station downStation = Station.create("하남풍산역");
        Line line = Line.create("5호선", "purple", upStation, downStation, 10);

        //when
        List<Station> stations = line.stationsFromUpToDown();

        //then
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.get(0)).isEqualTo(upStation);
        assertThat(stations.get(1)).isEqualTo(downStation);
    }
}
