package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("노선 테스트")
@DataJpaTest
public class LineTest {

    @Test
    void 노선_등록() {
        // When
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));

        // then
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
    }

    @Test
    void 노선_조회() {
        // given
        Line line = new Line("신분당선", "빨강색");
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).contains(upStation, downStation);
    }

    @Test
    void 노선_삭제() {
        // given
        Line line = new Line("신분당선", "빨강색");
        Station upStation = new Station("판교역");
        Station downStation = new Station("강남역");
        Section section = new Section(upStation, downStation, new Distance(10));
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).contains(upStation, downStation);
    }

}
