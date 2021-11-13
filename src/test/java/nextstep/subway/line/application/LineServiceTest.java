package nextstep.subway.line.application;

import nextstep.subway.global.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("LineService 테스트")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private Station 강남역;
    private Station 역삼역;
    private Station 사당역;
    private LineResponse 이호선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        사당역 = stationRepository.save(new Station("사당역"));
        이호선 = lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));
    }

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void saveLine() {
        // given
        LineRequest 사호선 = new LineRequest("4호선", "blue", 강남역.getId(), 사당역.getId(), 10);

        // when
        LineResponse lineResponse = lineService.saveLine(사호선);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(사호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(사호선.getColor())
        );
    }

    @Test
    @DisplayName("이름이 동일한 지하철 노선을 저장하면 예외가 발생한다.")
    void saveLineThrowException() {
        // when & then
        assertThatExceptionOfType(LineNameDuplicatedException.class)
                .isThrownBy(() -> lineService.saveLine(new LineRequest(이호선.getName(), "blue", 강남역.getId(), 사당역.getId(), 10)))
                .withMessageMatching(LineNameDuplicatedException.MESSAGE);
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void getLines() {
        // given
        Line 사호선 = lineRepository.save(new Line("4호선", "blue"));

        // when
        List<LineResponse> lineResponses = lineService.getLines();

        // then
        List<Long> lineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(lineIds).containsExactly(이호선.getId(), 사호선.getId());
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLine() {
        // when
        LineResponse lineResponse = lineService.getLine(이호선.getId());

        // then
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(이호선.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo(이호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(이호선.getColor()),
                () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
                () -> assertThat(lineResponse.getModifiedDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        LineRequest updateLineRequest = new LineRequest("3호선", "orange", 사당역.getId(), 역삼역.getId(), 10);

        // when
        lineService.updateLine(이호선.getId(), updateLineRequest);

        // then
        LineResponse lineResponse = lineService.getLine(이호선.getId());
        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(이호선.getId()),
                () -> assertThat(lineResponse.getName()).isEqualTo(updateLineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(updateLineRequest.getColor()),
                () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
                () -> assertThat(lineResponse.getModifiedDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lineService.getLine(이호선.getId()))
                .withMessageContaining(EntityNotFoundException.MESSAGE)
                .withMessageContaining("Line");
    }
}
