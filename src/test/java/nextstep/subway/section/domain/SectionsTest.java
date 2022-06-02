package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SectionsTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @Test
    @DisplayName("첫 노선 생성시 구간을 추가할 수 있다.")
    void createSection_addForInitLine() {

        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");

        Line line = new Line();
        line.update("2호선", "bg-blue-500");

        Section section = new Section(upStation, downStation, new Distance(10L));

        Sections sections = new Sections();
        sections.addForInit(section, line);

        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("종점역이 노선에 등록되어있지 않는 경우 에러출력")
    void createSection_add_fail() {

        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Line line = lineRepository.save(new Line("2호선", "bg-blue-500", section));

        Station newUpStation = new Station("봉천역");
        Station newDownStation = new Station("낙성대역");
        Section newSection = new Section(newUpStation, newDownStation, new Distance(5L));

        assertThatIllegalArgumentException()
            .isThrownBy(() -> line.addSection(newSection));
    }

    @Test
    @DisplayName("이미 상하행종점역이 모두 노선에 존재하는 경우 에러 출력")
    void createSection_add_fail2() {

        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Line line = lineRepository.save(new Line("2호선", "bg-blue-500", section));

        Section newSection = new Section(downStation, upStation, new Distance(5L));

        Sections sections = new Sections();

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(newSection, line));
    }

    @Test
    @DisplayName("정상적으로 구간이 등록되었을 경우 구간의 개수가 증가한다.")
    void createSection_add() {

        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        Station newUpStation = new Station("봉천역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newUpStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Line line = lineRepository.save(new Line("2호선", "bg-blue-500", section));

        Section newSection = new Section(upStation, newUpStation, new Distance(5L));

        line.addSection(newSection);
        assertThat(line.sizeSections()).isEqualTo(2);
    }



}
