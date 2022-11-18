package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.subway.common.exception.ErrorEnum;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("노선 테스트")
@DataJpaTest
public class LineRepositoryTest {

    final String 노선_이름 = "신분당선";
    final String 색상 = "bg-red-600";

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    void 노선_등록() {
        // Given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("수서역"));
        List<Station> sections = new ArrayList<>();
        sections.add(upStation);
        sections.add(downStation);

        // When
        Line 노선 = 노선_등록(노선_이름, 색상, upStation, downStation);

        // Then
        List<Station> 노선_포함_역 = new ArrayList<>(노선.getStations());
        assertAll(
                () -> assertThat(노선.getId()).isNotNull(),
                () -> assertThat(노선_포함_역).containsAll(sections)
        );
    }

    @Test
    void 노선_조회() {
        // Given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("수서역"));
        List<Station> sections = new ArrayList<>();
        sections.add(upStation);
        sections.add(downStation);
        Line 노선 = 노선_등록(노선_이름, 색상, upStation, downStation);

        // When
        Line 조회된_노선 = 노선_조회(노선.getId());

        // Then
        List<Station> 노선_포함_역 = new ArrayList<>(노선.getStations());
        assertAll(
                () -> assertThat(노선.getId()).isNotNull(),
                () -> assertThat(노선_포함_역).containsAll(sections)
        );
    }

    private Line 노선_조회(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException(ErrorEnum.NOT_EXISTS_LINE.message()));
    }

    @Test
    void 노선_삭제() {
        // Given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("수서역"));
        List<Station> sections = new ArrayList<>();
        sections.add(upStation);
        sections.add(downStation);
        Line 노선 = 노선_등록(노선_이름, 색상, upStation, downStation);

        // When
        노선_삭제(노선.getId());

        // Then
        assertThatThrownBy(() -> 노선_조회(노선.getId())).isInstanceOf(
                RuntimeException.class);

    }

    private void 노선_삭제(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    private Line 노선_등록(String 노선_이름, String 색상, Station upStation, Station downStation) {
        Line line = new Line(노선_이름, 색상);
        Section section = new Section(upStation, downStation, new Distance(12));
        line.addSection(section);
        return lineRepository.save(line);
    }
}
