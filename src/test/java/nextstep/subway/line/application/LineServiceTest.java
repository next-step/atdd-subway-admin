package nextstep.subway.line.application;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class LineServiceTest extends AcceptanceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Test
    void findAll() {
        // given
        saveLine("강남역", "잠실역", "2호선", "green", 3);
        saveLine("천호역", "방화", "5호선", "purple", 5);

        // when
        List<LineResponse> lineResponses = lineService.findAll();

        // then
        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        saveLine("강남역", "잠실역", "2호선", "green", 3);

        // when
        LineResponse lineResponse = lineService.findById(1L);
        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }


    @DisplayName("노선이 없는 경우 조회하면 예외 발생")
    @Test
    void findByIdThrow() {
        assertThatThrownBy(() -> {
            lineService.findById(1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("노선이 없는 경우 수정하면 예외 발생")
    @Test
    void updateThrow() {
        assertThatThrownBy(() -> {
            lineService.update(1L, new LineRequest("2호선", "black"));
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("노선이 없는 경우 삭하면 예외 발생")
    @Test
    void deleteThrow() {
        assertThatThrownBy(() -> {
            lineService.delete(1L);
        }).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("노선 생성시, 종점 추가")
    @Test
    void newSave() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 잠실역 = stationRepository.save(new Station("잠실역"));

        LineResponse lineResponse = lineService.save(new LineRequest("2호선", "green", 강남역.getId(), 잠실역.getId(), 3));
        assertThat(lineResponse.getStationsResponses().size()).isEqualTo(2);
    }

    private void saveLine(String upStation, String downStation, String name, String color, int distance) {
        Station 상행역 = stationRepository.save(new Station(upStation));
        Station 하행역 = stationRepository.save(new Station(downStation));
        lineService.save(new LineRequest(name, color, 상행역.getId(), 하행역.getId(), distance));
    }
}