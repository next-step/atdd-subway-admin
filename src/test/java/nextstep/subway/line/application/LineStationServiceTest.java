package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import nextstep.subway.application.LineService;
import nextstep.subway.application.LineStationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class LineStationServiceTest {

    @Autowired
    LineStationService lineStationService;

    @Autowired
    LineService lineService;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    Station station1;
    Station station2;
    Line line;

    @BeforeEach
    void beforeEach(){
        station1 = stationRepository.save(new Station("잠실역"));
        station2 = stationRepository.save(new Station("교대역"));
        LineResponse saveLine = lineService.saveLine(
                new LineRequest("2호선", "bg-color-060", station1.getId(), station2.getId(), 10));
        line = lineRepository.findById(saveLine.getId()).get();
    }

    @Test
    @DisplayName("상행 종점을 추가한다.")
    void addSectionBeforeFirstStation(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // when - 새로운 역 구간 추가 ( 새로운역 출발, 상행종점 도착)
        Station newStation = stationRepository.save(new Station("사당역"));
        lineStationService.addSection(line.getId(), new SectionRequest(newStation.getId(), station1.getId(), 10));

        // then - 역이 3개, 역 정보 조회는 일단 대기
        assertThat(line.getLineStations()).hasSize(3);
    }

    @Test
    @DisplayName("하행 종점을 추가한다.")
    void addSectionAfterLastStation(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // when - 새로운 역 구간 추가 ( 하행종점 출발, 새로운 역 도착)
        Station newStation = stationRepository.save(new Station("사당역"));
        lineStationService.addSection(line.getId(), new SectionRequest(station2.getId(), newStation.getId(), 10));

        // then - 역이 3개, 역 정보 조회는 일단 대기
        assertThat(line.getLineStations()).hasSize(3);
    }

    @Test
    @DisplayName("출발역이 기존역인 구간을 추가한다.")
    void addSectionIncludeUpStation(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // when - 새로운 역 구간 추가 ( 하행종점 출발, 새로운 역 도착)
        Station newStation = stationRepository.save(new Station("사당역"));
        lineStationService.addSection(line.getId(), new SectionRequest(station1.getId(), newStation.getId(), 10));

        // then - 역이 3개, 역 정보 조회는 일단 대기
        assertThat(line.getLineStations()).hasSize(3);
    }

    @Test
    @DisplayName("도착역이 기존역인 구간을 추가한다.")
    void addSectionIncludeDownStation(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // when - 새로운 역 구간 추가 ( 하행종점 출발, 새로운 역 도착)
        Station newStation = stationRepository.save(new Station("사당역"));
        lineStationService.addSection(line.getId(), new SectionRequest(newStation.getId(), station2.getId(), 10));

        // then - 역이 3개, 역 정보 조회는 일단 대기
        assertThat(line.getLineStations()).hasSize(3);
    }

    @Test
    @DisplayName("출발역과 도착역이 모두 기존에 존재하는 역입니다.")
    void addSectionAllIncludeException(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // then
        assertThatIllegalArgumentException().isThrownBy(
                () -> lineStationService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 10))
        );
    }

    @Test
    @DisplayName("출발역과 도착역이 모두 존재하지 않는 역입니다.")
    void addSectionAllNotIncludeException(){
        // given - 역 2개 / 노선 1개 추가 :beforeEach 처리

        // when
        Station newStation1 = stationRepository.save(new Station("서울대입구역"));
        Station newStation2 = stationRepository.save(new Station("사당역"));

        // then
        assertThatIllegalArgumentException().isThrownBy(
                () -> lineStationService.addSection(line.getId(), new SectionRequest(newStation1.getId(), newStation2.getId(), 10))
        );
    }
}
