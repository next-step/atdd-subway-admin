package nextstep.subway.line;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("노선 저장")
    @Test
    public void save() {
        //given
        //when
        Line savedLine = 노선저장("새로운노선", "새로운색");

        //then
        Line findLine = lineRepository.findById(savedLine.id()).orElseThrow(() -> new NoSuchDataException());
        assertThat(findLine).isEqualTo(savedLine);
    }

    @DisplayName("노선 수정")
    @Test
    public void update() {
        //given
        Line savedLine = 노선저장("새로운노선", "새로운색");

        //when
        savedLine.change("변경된노선이름", "변경된색이름");
        lineRepository.save(savedLine);
        entityManager.flush();
        entityManager.clear();

        //then
        Line findLine = lineRepository.findById(savedLine.id()).orElseThrow(() -> new NoSuchDataException());
        assertThat(findLine.name()).isEqualTo("변경된노선이름");
    }

    @DisplayName("노선 삭제")
    @Test
    public void delete() {
        //given
        Line savedLine = 노선저장("새로운노선", "새로운색");

        //when
        lineRepository.delete(savedLine);
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(lineRepository.findById(savedLine.id())).isEqualTo(Optional.empty());
    }

    @DisplayName("노선 등록 - 역, 구간과 함께 등록시 연관관계 확인")
    @Test
    public void 역과구간과함께노선생성시_연관관계확인() throws Exception {
        //given
        Station upStation = Station.create("상행종점역");
        Station downStation = Station.create("하행종점역");
        Section section = Section.create(100);
        Station savedUpStation = stationRepository.save(upStation);
        Station savedDownStation = stationRepository.save(downStation);
        Section savedSection = sectionRepository.save(section);

        //when
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", section, upStation, downStation);
        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        //then
        Line findLine = lineRepository.findById(savedLine.id()).orElseThrow(() -> new IllegalStateException());
        Section findSection = sectionRepository.findById(savedSection.id()).orElseThrow(() -> new IllegalStateException());
        Station findUpStation = stationRepository.findById(savedUpStation.id()).orElseThrow(() -> new IllegalStateException());
        Station findDownStation = stationRepository.findById(savedDownStation.id()).orElseThrow(() -> new IllegalStateException());

        assertThat(findLine.sortedStationList()).containsExactly(findUpStation, findDownStation);
        assertThat(findLine.sectionList()).contains(findSection);
        assertThat(findSection.upStation()).isEqualTo(findUpStation);
        assertThat(findSection.downStation()).isEqualTo(findDownStation);
        assertThat(findUpStation.downSection()).isEqualTo(findSection);
        assertThat(findDownStation.upSection()).isEqualTo(findSection);
    }

    /*@DisplayName("노선 저장시 구간 저장 확인")
    @Test
    public void save2() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        entityManager.flush();
        entityManager.clear();

        //when
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        entityManager.flush();
        entityManager.clear();

        //then
        Line line = lineRepository.findById(savedLine.id()).orElseThrow(() -> new NoSuchDataException());
        assertThat(line.sectionsSize()).isEqualTo(1);
    }

    @DisplayName("노선 저장시 역구간 저장 확인")
    @Test
    public void save3() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        entityManager.flush();
        entityManager.clear();

        //when
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        entityManager.flush();
        entityManager.clear();

        //then
        Line line = lineRepository.findById(savedLine.id()).orElseThrow(() -> new NoSuchDataException());
        Section section = line.sections().toList().get(0);

        assertThat(section.stationSectionsSize()).isEqualTo(2);
    }

    @DisplayName("노선 삭제시 구간 삭제 확인")
    @Test
    public void delete() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        Section section = savedLine.sections().toList().get(0);
        entityManager.flush();
        entityManager.clear();

        //when
        lineRepository.deleteById(savedLine.id());
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(sectionRepository.findById(section.id())).isEqualTo(Optional.empty());
    }

    @DisplayName("노선 이름 중복 확인: 중복인 경우")
    @Test
    public void existsByName() {
        //given
        노선저장("testName", "testColor");

        //when
        boolean isExits = lineRepository.existsByName("testName");

        //then
        assertThat(isExits).isTrue();
    }

    @DisplayName("노선 이름 중복 확인: 중복이 아닌 경우")
    @Test
    public void existsByName2() {
        //given
        노선저장("testName", "testColor");

        //when
        boolean isExits = lineRepository.existsByName("testName123");

        //then
        assertThat(isExits).isFalse();
    }

    Line 노선저장2(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = Line.create(name, color, upStation, downStation, distance);

        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }*/

    Line 노선저장(String name, String color) {
        Line line = Line.create(name, color);

        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }
}
