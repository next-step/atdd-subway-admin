package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("구간 생성")
    void createSection() {
        Station upStation = Station.from("신논현역");
        Station downStation = Station.from("동작역");
        Distance distance = Distance.from(10);
        Section actual = Section.of(upStation, downStation, distance);

        assertThat(actual.getUpStation()).isEqualTo(upStation);
        assertThat(actual.getDownStation()).isEqualTo(downStation);
        assertThat(actual.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("구간 생성 필수값 검증 (이름, 거리)")
    void createSectionException() {
        Station station = Station.from("동작역");
        Distance distance = Distance.from(10);

        assertThrows(IllegalArgumentException.class, () -> Section.of(null, station, distance));
        assertThrows(IllegalArgumentException.class, () -> Section.of(station, null, distance));
    }

    @Test
    @DisplayName("기존역과 상행/하행역이 모두 같으면 구간 생성 불가능")
    void sameSection() {
        Line line = Line.of("신분당선", "bg-yellow-600", Section.of(Station.from("신논현역"), Station.from("동작역"), Distance.from(10)));
        Station upStation = Station.from("신논현역");
        Station downStation = Station.from("동작역");
        Distance distance = Distance.from(5);
        assertThatThrownBy(() -> line.addSection(Section.of(upStation, downStation, distance)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역과 역 사이에 구간 추가")
    void addSectionBetweenStations() {
        Line line = Line.of("신분당선", "bg-yellow-600", Section.of(Station.from("신논현역"), Station.from("동작역"), Distance.from(10)));
        Station upStation = Station.from("신논현역");
        Station downStation = Station.from("신신논현역");
        Distance distance = Distance.from(5);
        line.addSection(Section.of(upStation, downStation, distance));

        List<Station> stations = line.getStations();
        assertThat(stations).hasSize(3);
        assertThat(line.totalDistance()).isEqualTo(15);
    }
}
