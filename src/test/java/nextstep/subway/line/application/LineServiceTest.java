package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineDto;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.station.application.StationServiceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LineServiceTest {



    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Test
    @Transactional
    void saveLine() {
        Station station1 = stationRepository.save(강남역.toStation());
        Station station2 = stationRepository.save(역삼역.toStation());
        LineDto.CreateRequest 이호선 = new LineDto.CreateRequest()
                .setColor("green")
                .setName("2호선")
                .setDistance(10);

        Line expect = lineRepository.save(이호선.of(station1, station2));
        assertThat(lineRepository.findById(expect.getId()).get()).isEqualTo(expect);
    }

    @Test
    @Transactional
    void findAllLines() {
        Station station1 = stationRepository.save(강남역.toStation());
        Station station2 = stationRepository.save(역삼역.toStation());
        LineDto.CreateRequest 이호선 = new LineDto.CreateRequest()
                .setColor("green")
                .setName("2호선")
                .setDistance(10);

        Station station3 = stationRepository.save(교대역.toStation());
        Station station4 = stationRepository.save(마두역.toStation());
        LineDto.CreateRequest 삼호선 = new LineDto.CreateRequest()
                .setColor("orange")
                .setName("3호선")
                .setDistance(10);

        lineRepository.save(이호선.of(station1, station2));
        lineRepository.save(삼호선.of(station1, station2));

        lineRepository.findAll();
        assertThat(lineRepository.findAll()).hasSize(2);



    }

    @Test
    @Transactional
    void findLineById() {
    }

    @Test
    @Transactional
    void deleteLineById() {
    }

    @Test
    @Transactional
    void updateLine() {
    }

}
