package nextstep.subway.line;

import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    TestEntityManager entityManager;

    @DisplayName("노선 저장")
    @Test
    public void save() {
        //given
        //when
        Line savedLine = 노선저장("testName", "testColor");

        //then
        Line findLine = lineRepository.findById(savedLine.getId()).orElseThrow(() -> new NoSuchDataException());
        assertThat(findLine).isEqualTo(savedLine);
    }

    @DisplayName("노선 저장시 구간 저장 확인")
    @Test
    public void save2() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");

        //when
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        entityManager.flush();
        entityManager.clear();

        //then
        Line line = lineRepository.findById(savedLine.getId()).orElseThrow(() -> new NoSuchDataException());
        assertThat(line.sectionsSize()).isEqualTo(1);
    }

    @DisplayName("노선 저장시 역구간 저장 확인")
    @Test
    public void save3() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");

        //when
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        entityManager.flush();
        entityManager.clear();

        //then
        Line line = lineRepository.findById(savedLine.getId()).orElseThrow(() -> new NoSuchDataException());
        Section section = line.sections().toList().get(0);

        assertThat(section.stationSectionsSize()).isEqualTo(2);
    }

    @DisplayName("노선 삭제시 구간 삭제 확인")
    @Test
    public void delete() {
        //given
        Station upStation = Station.create("상행역");
        Station downStation = Station.create("하행역");
        Line savedLine = 노선저장2("testName", "testColor", upStation, downStation, 10);
        Section section = savedLine.sections().toList().get(0);
        entityManager.flush();
        entityManager.clear();

        //when
        lineRepository.deleteById(savedLine.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(sectionRepository.findById(section.getId())).isEqualTo(Optional.empty());
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
    }

    Line 노선저장(String name, String color) {
        Line line = Line.create(name, color);

        Line savedLine = lineRepository.save(line);
        entityManager.flush();
        entityManager.clear();

        return savedLine;
    }
}
