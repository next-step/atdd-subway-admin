package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void save() {
        // when
        Line line = lineRepository.save(new Line("2호선", "green"));

        // then
        assertThat(line).isNotNull();
        assertThat(line.getId()).isNotNull();
    }

    @DisplayName("중복된 이름의 노선을 등록 하는 경우")
    @Test
    void saveWithNameAlreadyExist() {
        // given
        Line line = lineRepository.save(new Line("2호선", "green"));

        // when
        assertThatThrownBy(() -> {
            lineRepository.save(new Line("2호선", "green"));
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void findAll() {
        // given
        lineRepository.save(new Line("2호선", "green"));
        lineRepository.save(new Line("5호선", "purple"));

        // when
        List<Line> lines = lineRepository.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        Line expected = lineRepository.save(new Line("5호선", "purple"));

        // when
        Line actual = lineRepository.findById(expected.getId()).orElse(null);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual == expected).isTrue();
    }

    @Test
    void update() {
        // given
        Line line = lineRepository.save(new Line("5호선", "purple"));

        // when
        line.update(new Line("2호선", "green"));
        lineRepository.flush();

        assertThat(lineRepository.findById(line.getId()).get() == line).isTrue();
    }

    @Test
    void delete() {
        // given
        Line line = lineRepository.save(new Line("5호선", "purple"));

        // when
        lineRepository.delete(line);
        lineRepository.flush();

        assertThat(lineRepository.findById(line.getId())).isNotPresent();
    }

    @DisplayName("상행 종점과 하행 종점을 추가하여 노선을 생성한다.")
    @Test
    void createWithUpStationAndDownStation() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 시청역 = stationRepository.save(new Station("시청역"));
        // when
        Line savedLine = createLineWithUpStationAndDownStation(시청역, 강남역);

        // then
        Line acutal = lineRepository.findById(savedLine.getId()).get();
        Sections sections = acutal.getSections();
        assertThat(sections.getSections().size()).isEqualTo(1);
    }


    private Line createLineWithUpStationAndDownStation(Station upStation, Station downStation) {
        Line line = new Line("2호선", "green");

        Section section = Section.builder().upStation(upStation)
                .downStation(downStation)
                .line(line)
                .distance(50)
                .build();

        line.addSection(section);

        return lineRepository.save(line);
    }

    @DisplayName("[구간 추가 등록] 역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionBetweenUpStationAndDownStation() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Station B역 = stationRepository.save(new Station("B역"));
        Section section = Section.builder().upStation(B역)
                .downStation(C역)
                .line(line)
                .distance(42)
                .build();

        line.addSection(section);
        lineRepository.flush();

        assertThat(line.getSections().getStations()).containsExactlyElementsOf(Arrays.asList(A역, B역, C역));
   }

    @DisplayName("[구간 추가 등록] 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionNewUpStation() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Station B역 = stationRepository.save(new Station("B역"));
        Section section = Section.builder().upStation(B역)
                .downStation(A역)
                .line(line)
                .distance(42)
                .build();

        line.addSection(section);
        lineRepository.flush();

        assertThat(line.getSections().getStations()).containsExactlyElementsOf(Arrays.asList(B역, A역, C역));
    }

    @DisplayName("[구간 추가 등록] 새로운 역을 행 종점으로 등록할 경우")
    @Test
    void addSectionNewDownStation() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Station B역 = stationRepository.save(new Station("B역"));
        Section section = Section.builder().upStation(C역)
                .downStation(B역)
                .line(line)
                .distance(42)
                .build();

        line.addSection(section);
        lineRepository.flush();

        assertThat(line.getSections().getStations()).containsExactlyElementsOf(Arrays.asList(A역, C역, B역));
    }

    @DisplayName("[구간 추가 예외] 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void validateDistance() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Station B역 = stationRepository.save(new Station("B역"));
        Section section = Section.builder().upStation(B역)
                .downStation(C역)
                .line(line)
                .distance(55)
                .build();

        assertThatThrownBy(() -> {
            line.addSection(section);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("짧아야합니다.");
    }

    @DisplayName("[구간 추가 예외] 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void validateAlready() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Section section = Section.builder().upStation(A역)
                .downStation(C역)
                .line(line)
                .distance(50)
                .build();


        assertThatThrownBy(() -> {
            sectionRepository.save(section);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("[구간 추가 예외] 상행역과 하행역 둘중 하나도 포함되어 있지 않으면 추가할 수 없")
    @Test
    void validateContation() {
        Station C역 = stationRepository.save(new Station("C역"));
        Station A역 = stationRepository.save(new Station("A역"));
        Station B역 = stationRepository.save(new Station("B역"));
        Station D역 = stationRepository.save(new Station("D역"));
        Line line = createLineWithUpStationAndDownStation(A역, C역);
        Section section = Section.builder().upStation(B역)
                .downStation(D역)
                .line(line)
                .distance(48)
                .build();


        assertThatThrownBy(() -> {
            line.addSection(section);
        }).isInstanceOf(IllegalArgumentException.class);
    }



}