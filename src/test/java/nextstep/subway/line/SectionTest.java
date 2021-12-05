package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

@DataJpaTest
public class SectionTest {

    @DisplayName("구간의 하행역을 추가되는 구간의 상행역으로 수정")
    @Test
    void updateDownStation() {
        // given
        Line line = new Line("신분당선", "red");
        Station upStation = new Station("1번역");
        Station downStation = new Station("2번역");
        Section section = new Section(line, upStation, downStation, 10);

        Station addUpStation = new Station("3번역");
        Station addDownStation = new Station("4번역");
        Section addSection = new Section(line, addUpStation, addDownStation, 3);

        section.updateDownStation(addSection);

        // when
        Station changeStation = section.getDownStation();

        // then
        assertThat(changeStation).isEqualTo(addUpStation);
    }

    @DisplayName("구간의 상행역을 추가되는 구간의 하행역으로 수정")
    @Test
    void updateUpStation() {
        // given
        Line line = new Line("신분당선", "red");
        Station upStation = new Station("1번역");
        Station downStation = new Station("2번역");
        Section section = new Section(line, upStation, downStation, 10);

        Station addUpStation = new Station("3번역");
        Station addDownStation = new Station("4번역");
        Section addSection = new Section(line, addUpStation, addDownStation, 3);

        section.updateUpStation(addSection);

        // when
        Station changeStation = section.getUpStation();

        // then
        assertThat(changeStation).isEqualTo(addDownStation);
    }
}
