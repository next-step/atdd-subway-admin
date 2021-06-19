package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.exception.ConflictException;
import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.exception.NotExistStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
class LineServiceTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @Test
    @DisplayName(value = "새로운 Line 을 저장하면 DB에 반영된다")
    void createLine() {
        // given
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));

        // when: 신분당선 생성 요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        LineResponse response = lineService.saveLine(request);

        // then: 신분당선 생성과 section 2개 생성 완료
        assertThat(response.getColor()).isEqualTo("bg-red-600");
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @Test
    @DisplayName(value = "Line을 저장할 경우 station이 없다면, NotExistStationException을 일으킨다")
    void errorNotExistStation() {
        // given: station이 없다

        // when: Line 생성 요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // then: station 이 없으므로 NotExistStationException 이 발생된다
        assertThrows(NotExistStationException.class,
            () -> lineService.saveLine(request));
    }

    @Test
    @DisplayName(value = "이미 저장된 Line 을 저장하면 error 을 반환한다")
    void errorWhenAlreadyExist() {
        // given1: 강남역과 역삼역이 존재한다
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));

        // given2: 강남역과 역삼역을 상행, 하행으로 하는 신분당선을 생성 요청한다
        LineRequest request = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        lineService.saveLine(request);

        // when: 동일한 요청을 한번 더 한다
        // then: ConflictException 이 발생한다
        assertThrows(ConflictException.class, () -> lineService.saveLine(request));
    }

    @Test
    @DisplayName(value = "Line 수정을 실행하면 DB에 반영되어 수정된 결과가 나타난다")
    void updateLine() {
        // given1: 강남역과 역삼역이 주어졌다
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));

        // given2: 1호선이 상행, 하생으로 강남역 역삼역을 갖도록 요청한다
        LineRequest request = new LineRequest("1호선", "blue", station1.getId(), station2.getId(), 10);
        LineResponse line = lineService.saveLine(request);

        // when: 요청이 잘못되어 수정한다
        lineService.updateLine(line.getId(), new LineRequest("2호선", "bg-green-100", station1.getId(), station2.getId(), 10));

        // then: 올바른 Line 으로 수정된다
        Line updated = lineRepository.findById(line.getId()).get();
        assertThat(updated.getName()).isEqualTo("2호선");
        assertThat(updated.getColor()).isEqualTo("bg-green-100");
    }

    @Test
    @DisplayName(value = "요청한 Line 이 없을 경우 update 가 되지 않고 NotExistLineException 을 반환한다")
    void notExistWhenUpdate() {
        // given: 현재 저장된 Line 이 없다
        // when: 어떠한 Line 을 지우고자 요청을 보낸다
        // then: NotExistLineException 이 발생한다
        assertThrows(NotExistLineException.class, () ->
            lineService.updateLine(1L, new LineRequest("1호선", "yellow", 1L, 2L, 10)));
    }

    @Test
    @DisplayName(value = "Line 을 제거하는 service 를 실행하면 DB에 반영된다")
    void deleteLine() {
        // given: 강남역, 역삼역이 주어져 있다
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));
        // given: 1호선이 강남역과 역삼역을 상행 하행으로 생성 요청한다
        LineRequest request = new LineRequest("1호선", "blue", station1.getId(), station2.getId(), 10);
        LineResponse line = lineService.saveLine(request);

        // when: 잘못된 Line 이므로 삭제요청한다
        lineService.delete(line.getId());

        // then: 삭제된다
        assertThat(lineRepository.findById(line.getId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName(value = "요청한 Line 이 없을 경우 Delete 가 실행되지 않고 NotExistLineException 을 발생시킨다")
    void notExistWhenDelete() {
        // given: 존재하는 Line 이 없다
        // when: 10번 Line을 삭제하고자 한다
        // then: NotExistLineException 이 발생한다
        assertThrows(NotExistLineException.class, () ->
            lineService.delete(10L));
    }

}