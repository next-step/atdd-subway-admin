package nextstep.subway.linebridge.application;

import static org.assertj.core.api.Assertions.*;

import nextstep.subway.distance.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.linebridge.domain.LineBridge;
import static nextstep.subway.station.application.StationServiceTest.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@DataJpaTest
class LineBridgeServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @Transactional
    void 구간만들고_노선생성() {
        Station upStation = stationRepository.save(지하철_생성("강남역"));
        Station downStation = stationRepository.save(지하철_생성("역삼역"));
        int distance = 10;
        LineBridge lineBridge = new LineBridge(upStation, downStation, distance);
        Line line = 노선_생성(lineBridge);

        Line expect = lineRepository.save(line);
        assertThat(lineRepository.findById(expect.getId()).get().getLineBridges()).hasSize(1);
    }

    @Test
    @Transactional
    void 구간추가하기 () {
        Station upStation = stationRepository.save(지하철_생성("강남역"));
        Station downStation = stationRepository.save(지하철_생성("역삼역"));
        LineBridge lineBridge = new LineBridge(upStation, downStation, new Distance(10).getValue());
        Line line = 노선_생성(lineBridge);
        Line expect = lineRepository.save(line);
        Station upStation2 = stationRepository.save(지하철_생성("교대역"));

        LineBridge lineBridge2 = new LineBridge(upStation2, downStation, new Distance(5).getValue());
        line.addLineBridge(lineBridge2);
        lineRepository.findById(line.getId());
        assertThat(lineRepository.findById(expect.getId()).get().getLineBridges()).hasSize(2);
    }

    @Test
    @Transactional
    void 기존구간에_겹치는_구간이_없으면_예외처리() {
        Station upStation = stationRepository.save(지하철_생성("강남역"));
        Station downStation = stationRepository.save(지하철_생성("역삼역"));
        LineBridge lineBridge = new LineBridge(upStation, downStation, new Distance(10).getValue());
        Line line = 노선_생성(lineBridge);

        Station upStation2 = stationRepository.save(지하철_생성("교대역"));
        Station downStation2 = stationRepository.save(지하철_생성("방배역"));

        LineBridge lineBridge2 = new LineBridge(upStation2, downStation2, new Distance(5).getValue());

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            line.addLineBridge(lineBridge2);
        });

    }

    @Test
    @Transactional
    void 구간삭제하기() {
        Station upStation = stationRepository.save(지하철_생성("강남역"));
        Station downStation = stationRepository.save(지하철_생성("역삼역"));
        LineBridge lineBridge = new LineBridge(upStation, downStation, new Distance(10).getValue());
        Line line = 노선_생성(lineBridge);
        Station upStation2 = stationRepository.save(지하철_생성("교대역"));
        LineBridge lineBridge2 = new LineBridge(upStation2, downStation, new Distance(5).getValue());
        line.addLineBridge(lineBridge2);

        Line expect = lineRepository.save(line);
        assertThat(lineRepository.findById(line.getId()).get().getLineBridges()).hasSize(2);

        line.removeStation(upStation2);
        assertThat(lineRepository.findById(line.getId()).get().getLineBridges()).hasSize(1);
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            line.removeStation(upStation);
        });

    }


    private Line 노선_생성 (LineBridge lineBridge) {

        return new Line.Builder()
            .setColor("2호선")
            .setName("green")
            .setLineBridge(lineBridge).build();
    }
}
