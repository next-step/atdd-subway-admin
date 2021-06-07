package nextstep.subway.section;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sections 일급콜렉션 클래스 관련 테스트")
public class SectionsTest {

    @Test
    void create() {
        Sections sections = new Sections();

        assertThat(sections.stream().count()).isZero();
    }

    @Test
    void Section을_추가한다() {
        Sections sections = new Sections();

        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;
        Section section = Section.of(upStation, downStation, distance);

        sections.add(section);

        assertThat(sections.stream().count()).isOne();
    }

    @Test
    void Section을_중복_추가는_영향없다() {
        Sections sections = new Sections();

        Station upStation = Station.of("상행선");
        Station downStation = Station.of("하행선");
        int distance = 5;
        Section section = Section.of(upStation, downStation, distance);

        sections.add(section);
        sections.add(section);

        assertThat(sections.stream().count()).isOne();
    }

}
