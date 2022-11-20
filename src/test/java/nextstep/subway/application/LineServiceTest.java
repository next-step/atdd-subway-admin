package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    LineService lineService;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    EntityManager em;

    Station station1 = null;
    Station station2 = null;
    Station station3 = null;
    LineRequest lineRequest = null;

    @BeforeEach
    void beforeEach() {
        station1 = stationRepository.save(new Station("경기 광주역"));
        station2 = stationRepository.save(new Station("중앙역"));
        lineRequest = new LineRequest("신분당선", "bg-red-600", 10, station1.getId(), station2.getId());
    }

    @Test
    void saveLine() {
        Long id = lineService.saveLine(lineRequest);

        LineResponse actual = lineService.findResponseById(id);
        assertThat(actual.getName()).isEqualTo(lineRequest.getName());
    }

    @Test
    void findAllLines() {
        lineService.saveLine(lineRequest);

        List<LineResponse> allLines = lineService.findAllLines();

        assertThat(allLines).hasSize(1);
    }

    @Test
    void findById() {
        Long id = lineService.saveLine(lineRequest);

        assertThatNoException().isThrownBy(() -> lineService.findResponseById(id));
    }

    @Test
    void updateLine() {
        Long id = lineService.saveLine(lineRequest);
        LineRequest request = new LineRequest("신분당선2", "bg-green-600", 10, station1.getId(), station2.getId());

        lineService.updateLine("신분당선", request);

        LineResponse findLine = lineService.findResponseById(id);
        assertThat(findLine.getName()).isEqualTo("신분당선2");
        assertThat(findLine.getColor()).isEqualTo("bg-green-600");
    }

    @Test
    void deleteLineById() {
        Long lineId = lineService.saveLine(lineRequest);

        lineService.deleteLineById(lineId);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lineService.findResponseById(lineId));
    }

    @DisplayName("기존 노선에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        Long lineId = lineService.saveLine(lineRequest);
        station3 = stationRepository.save(new Station("모란역"));
        flushAndClear();

        lineService.addSection(lineId, new SectionRequest(station1.getId(), station3.getId(), 4));
        flushAndClear();

        SectionResponse response = lineService.findSectionResponsesByLineId(lineId);

        assertThat(response.getDistances()).containsExactly(4, 6);
        assertThat(response.getSortNos()).containsExactly("경기 광주역", "모란역", "중앙역");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
