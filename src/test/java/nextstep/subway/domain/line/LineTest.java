package nextstep.subway.domain.line;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class LineTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("노선의 종착역을 설정한다.")
    @Test
    void setTerminus() {
        // given
        Station upStation = stationRepository.save(Station.create("당고개역"));
        Station downStation = stationRepository.save(Station.create("오이도역"));
        Line line = Line.create("4호선", "하늘색", 10);

        // when
        line.setTerminus(upStation, downStation);

        // then
        assertThat(line.getUpStation()).isEqualTo(upStation);
        assertThat(line.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("노선의 종착역을 여러번 설정하면 에러가 발생한다.")
    @Test
    void setTerminus_여러번_호출() {
        // given
        Station upStation = stationRepository.save(Station.create("당고개역"));
        Station downStation = stationRepository.save(Station.create("오이도역"));
        Line line = Line.create("4호선", "하늘색", 10);

        line.setTerminus(upStation, downStation);

        // when, then
        assertThatThrownBy(() -> line.setTerminus(upStation, downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Station upStation = stationRepository.save(Station.create("당고개역"));
        Station downStation = stationRepository.save(Station.create("오이도역"));
        Section section = Section.create(upStation, downStation, 10);
        Line line = Line.create("4호선", "하늘색", 10);

        // when
        line.addSection(section);

        // then
        List<Section> sections = line.getSections().getSections();
        assertThat(sections).containsExactly(section);
    }

    @DisplayName("동일 구간을 추가하면 에러가 발생한다.")
    @Test
    void addSection_동일구간_추가() {
        // given
        Station upStation = stationRepository.save(Station.create("당고개역"));
        Station downStation = stationRepository.save(Station.create("오이도역"));
        Section section = Section.create(upStation, downStation, 10);
        Line line = Line.create("4호선", "하늘색", 10);

        line.addSection(section);

        // when, then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }
}