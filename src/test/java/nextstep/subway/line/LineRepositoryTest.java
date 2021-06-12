package nextstep.subway.line;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.linestation.LineStationRepository;
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
    private LineStationRepository lineStationRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("노선 저장")
    @Test
    public void save() {
        //given
        Station upStation = Station.create("upStation");
        Station downStation = Station.create("downStation");
        stationRepository.save(upStation);
        stationRepository.save(downStation);

        Section section = Section.create(100);
        entityManager.flush();
        entityManager.clear();

        //when
        Line savedLine = 노선저장2("새로운노선", "새로운색", upStation, downStation, section);

        //then
        Line findLine = lineRepository.findById(savedLine.id()).orElseThrow(() -> new NoSuchDataException());
        assertThat(findLine).isEqualTo(savedLine);
    }

    @DisplayName("노선 수정")
    @Test
    public void update() {
        //given
        Station upStation = Station.create("upStation");
        Station downStation = Station.create("downStation");
        stationRepository.save(upStation);
        stationRepository.save(downStation);

        Section section = Section.create(100);
        entityManager.flush();
        entityManager.clear();

        Line savedLine = 노선저장2("새로운노선", "새로운색", upStation, downStation, section);

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
        Station saveUpStation = stationRepository.save(Station.create("upStation"));
        Station saveDownStation = stationRepository.save(Station.create("downStation"));
        Section saveSection = sectionRepository.save(Section.create(100));
        entityManager.flush();
        entityManager.clear();

        Line line = 노선저장2("새로운노선", "새로운색", saveUpStation, saveDownStation, saveSection);
        Line findLine = lineRepository.findById(line.id()).orElseThrow(() -> new IllegalStateException());

        //when
        findLine.disconnectAll();
        findLine.sections().list().forEach(section -> sectionRepository.delete(section));
        findLine.lineStations().list().forEach(lineStation -> lineStationRepository.delete(lineStation));
        findLine.lineStations().list().forEach(lineStation -> stationRepository.delete(lineStation.station()));
        lineRepository.deleteById(findLine.id());
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(lineRepository.findById(findLine.id())).isEqualTo(Optional.empty());
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
        Line line = Line.createWithSectionAndStation("테스트노선", "테스트색", savedSection, savedUpStation, savedDownStation);
        lineStationRepository.save(LineStation.create(line, savedUpStation));
        lineStationRepository.save(LineStation.create(line, savedDownStation));
        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        //then
        Line findLine = lineRepository.findById(savedLine.id()).orElseThrow(() -> new IllegalStateException());
        Section findSection = sectionRepository.findById(savedSection.id()).orElseThrow(() -> new IllegalStateException());
        Station findUpStation = stationRepository.findById(savedUpStation.id()).orElseThrow(() -> new IllegalStateException());
        Station findDownStation = stationRepository.findById(savedDownStation.id()).orElseThrow(() -> new IllegalStateException());

        assertThat(findLine.sortedStationList()).containsExactly(findUpStation, findDownStation);
        assertThat(findLine.sections().contains(findSection)).isTrue();
        assertThat(findSection.downStation()).isEqualTo(findDownStation);
        assertThat(findUpStation.downSection()).isEqualTo(findSection);
        assertThat(findDownStation.upSection()).isEqualTo(findSection);
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

    Line 노선저장2(String name, String color, Station upStation, Station downStation, Section section) {
        Line line = Line.createWithSectionAndStation(name, color, section, upStation, downStation);
        lineStationRepository.save(LineStation.create(line, upStation));
        lineStationRepository.save(LineStation.create(line, downStation));
        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }

    Line 노선저장(String name, String color) {
        Line line = Line.create(name, color);

        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }
}
