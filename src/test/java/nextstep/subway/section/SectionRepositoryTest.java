package nextstep.subway.section;

import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station_section.StationSection;
import nextstep.subway.station_section.StationSectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SectionRepositoryTest {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationSectionRepository stationSectionRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    TestEntityManager entityManager;

    /**
     * 구간 저장시 -> 노선이 저장되어 있어야함. 역구간(2개), 역(2개) 자동 생성.
     * 노선 저장시 -> 구간(1개), 역구간(2개), 역(2개) 자동 생성.
     * 구간 삭제시 -> 역구간 삭제. 노선, 역은 삭제되면 안됨.
     * 노선 삭제시 -> 구간, 역구간 삭제. 역은 삭제되면 안됨.
     * 역은 다른 구간이 공유할 수 있음. 즉 하나의 역은 여러 구간을 가질 수 있음(환승역)
     */
    /**
     * 구간을 저장하는 두 가지 방법
     * 1. 이미 저장되어 있는 노선에 구간을 저장한다.
     * 2. 노선을 저장하면 종점역에 해당하는 구간이 하나 생긴다.
     */
    @DisplayName("구간 저장시 역구간 저장 확인")
    @Test
    public void 구간저장시_역구간저장확인() {
        //given
        Line line = Line.create("신분당선", "red");
        lineRepository.save(line);
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        Section section = Section.create(line, upStation, downStation, 10);

        //when
        Section savedSection = sectionRepository.save(section);
        entityManager.flush();
        entityManager.clear();

        //then
        Section findSection = sectionRepository.findById(savedSection.getId()).orElseThrow(() -> new NoSuchDataException());
        assertThat(findSection.stationSectionsSize()).isEqualTo(2);
    }

    @DisplayName("구간 삭제시 역구간 저장 확인")
    @Test
    public void 구간삭제시_역구간삭제확인() {
        //given
        Line line = Line.create("신분당선", "red");
        lineRepository.save(line);
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        Section section = Section.create(line, upStation, downStation, 10);
        Section savedSection = sectionRepository.save(section);
        List<StationSection> stationSections = savedSection.stationSections().toList();
        assertThat(stationSections.size()).isEqualTo(2);
        entityManager.flush();
        entityManager.clear();

        //when
        sectionRepository.deleteById(savedSection.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(stationSectionRepository.findById(stationSections.get(0).getId())).isEqualTo(Optional.empty());
        assertThat(stationSectionRepository.findById(stationSections.get(1).getId())).isEqualTo(Optional.empty());
    }
}
