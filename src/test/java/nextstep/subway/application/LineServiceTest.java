package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
    Station station4 = null;
    Station station5 = null;
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
        station4 = stationRepository.save(new Station("상행 종점"));
        station5 = stationRepository.save(new Station("하행 종점"));

        lineService.addSection(lineId, new SectionRequest(station1.getId(), station3.getId(), 4));
        lineService.addSection(lineId, new SectionRequest(station4.getId(), station1.getId(), 5));
        lineService.addSection(lineId, new SectionRequest(station2.getId(), station5.getId(), 7));
        flushAndClear();

        SectionResponse response = lineService.findSectionResponsesByLineId(lineId);
        assertThat(response.getStationNames()).containsExactly("상행 종점", "경기 광주역", "모란역", "중앙역", "하행 종점");
        assertThat(response.getDistances()).containsExactly(5, 4, 6, 7);
    }

    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가장 마지막역을 제거하는 경우 앞구간만 남는다")
    @Test
    void deleteLastSectionsAndDownStation() {
        Long lineId = lineService.saveLine(lineRequest);
        station3 = stationRepository.save(new Station("모란역"));
        lineService.addSection(lineId, new SectionRequest(station1.getId(), station3.getId(), 4));

        lineService.deleteSectionByStationId(lineId, station2.getId());

        SectionResponse response = lineService.findSectionResponsesByLineId(lineId);
        assertThat(response.getDistances()).containsOnly(4);
        assertThat(response.getStationNames()).containsExactly("경기 광주역", "모란역");
    }

    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가운데 역을 제거하는 경우 하나의 구간으로 합쳐진다")
    @Test
    void deleteBetweenStationOfSections() {
        Long lineId = lineService.saveLine(lineRequest);
        station3 = stationRepository.save(new Station("모란역"));
        lineService.addSection(lineId, new SectionRequest(station1.getId(), station3.getId(), 4));

        lineService.deleteSectionByStationId(lineId, station3.getId());

        SectionResponse response = lineService.findSectionResponsesByLineId(lineId);
        assertThat(response.getDistances()).containsOnly(10);
        assertThat(response.getStationNames()).containsExactly("경기 광주역", "중앙역");
    }

    @DisplayName("노선에 등록되어있지 않은 역을 제거하려 하면 EX 발생")
    @Test
    void deleteNotExistsStationOfSections() {
        Long lineId = lineService.saveLine(lineRequest);
        station3 = stationRepository.save(new Station("모란역"));
        Station station4 = stationRepository.save(new Station("미금역"));
        lineService.addSection(lineId, new SectionRequest(station1.getId(), station3.getId(), 4));

        ThrowingCallable deleteNotExistStation = () -> lineService
                .deleteSectionByStationId(lineId, station4.getId());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(deleteNotExistStation);
    }

    @DisplayName("구간이 하나인 노선의 하행에 해당하는 역을 제거하려 하면 EX 발생")
    @Test
    void deleteDownStationOfOneSectionThenThrow() {
        Long lineId = lineService.saveLine(lineRequest);

        ThrowingCallable deleteDownStationOfOneSection = () -> lineService
                .deleteSectionByStationId(lineId, station2.getId());

        assertThatIllegalArgumentException()
                .isThrownBy(deleteDownStationOfOneSection)
                .withMessageContaining("구간이 하나인 노선은 제거할 수 없습니다.");
    }

    @DisplayName("구간이 하나인 노선의 상행역을 제거하려 하면 EX 발생")
    @Test
    void deleteUpStationOfOneSectionThenThrow() {
        Long lineId = lineService.saveLine(lineRequest);

        ThrowingCallable deleteUpStationOfOneSection = () -> lineService
                .deleteSectionByStationId(lineId, station1.getId());

        assertThatIllegalArgumentException()
                .isThrownBy(deleteUpStationOfOneSection)
                .withMessageContaining("구간이 하나인 노선은 제거할 수 없습니다.");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
