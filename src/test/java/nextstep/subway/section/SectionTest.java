package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    @DisplayName("기존에 존재하는 섹션 사이에 새로운 역을 추가")
    void addSectionInExsistSection() {

        Line line = new Line("신분당선", "red");
        Station station1 = Station.of(1l, "강남역");
        Station station2 = Station.of(2l, "광교역");
        Station station3 = Station.of(3l, "강남광교사이역");

        line.addSection(Section.of(station1, station2, 10));

        Section newSection = Section.of(station1, station3, 3);
        line.addSection(newSection);

        assertThat(line.getOrderedStations()).contains(station1, station3, station2);

    }

}
