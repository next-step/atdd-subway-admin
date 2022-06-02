package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
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
    @DisplayName("종점역이 노선에 등록되어있지 않는 경우 에러출력")
    void createSection_add_fail() {
        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        Station newUpStation = new Station("봉천역");
        Station newDownStation = new Station("낙성대역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newDownStation, newUpStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Section newSection = new Section(newUpStation, newDownStation, new Distance(5L));

        Sections sections = new Sections();
        sections.add(section);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(newSection));
    }

    @Test
    @DisplayName("이미 상하행종점역이 모두 노선에 존재하는 경우 에러 출력")
    void createSection_add_fail2() {
        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Section newSection = new Section(downStation, upStation, new Distance(5L));

        Sections sections = new Sections();
        sections.add(section);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(newSection));
    }

    @Test
    @DisplayName("정상적으로 구간이 등록되었을 경우 구간의 개수가 증가한다.")
    void createSection_add() {
        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        Station newUpStation = new Station("봉천역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newUpStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Section newSection = new Section(upStation, newUpStation, new Distance(5L));

        Sections sections = new Sections();
        sections.add(section);
        sections.add(newSection);

        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("노선에 등록된 지하철역을 상하행 순서로 출력한다.")
    void orderStationsOfLine() {
        Station upStation = new Station("신림역");
        Station downStation = new Station("서울대입구역");
        Station newUpStation = new Station("봉천역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newUpStation));

        Section section = new Section(upStation, downStation, new Distance(10L));
        Section newSection = new Section(upStation, newUpStation, new Distance(5L));

        Sections sections = new Sections();
        sections.add(section);
        sections.add(newSection);

        assertThat(sections.orderStationsOfLine()).containsExactly(upStation, newUpStation, downStation);
    }

}
