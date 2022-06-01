package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private LineService lineService;

    private Station gangnam = new Station("강남역");
    private Station yangjae = new Station("양재역");

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);

        stationRepository.save(gangnam);
        stationRepository.save(yangjae);
    }

    @Test
    void LineRequest_객체를_파라미터로_노선을_생성하면_올바른_LineResponse_객체가_반환되어야_한다() {
        // given
        final String lineName = "신분당선";
        final String color = "bg_red-600";
        final LineRequest lineRequest = new LineRequest(lineName, color, gangnam.getId(), yangjae.getId());

        // when
        final LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getId()).isGreaterThan(0L);
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getStations())
                .containsExactly(StationResponse.of(gangnam), StationResponse.of(yangjae));
    }

    @Test
    void 노선_목록을_조회하면_모든_노선에_대해_LineResponse_객체_목록이_반환되어야_한다() {
        // given
        final Line line1 = new Line("신분당선", "bg-red-600");
        line1.relateToStation(new LineStation(line1, gangnam));
        line1.relateToStation(new LineStation(line1, yangjae));
        lineRepository.save(line1);

        final Line line2 = new Line("2호선", "bg-green-600");
        line2.relateToStation(new LineStation(line2, gangnam));
        lineRepository.save(line2);

        final Line line3 = new Line("3호선", "bg-orange-600");
        line3.relateToStation(new LineStation(line3, yangjae));
        lineRepository.save(line3);

        // when
        List<LineResponse> lines = lineService.findAllLines();

        // then
        assertThat(lines.stream().map(LineResponse::getName).collect(Collectors.toList()))
                .containsExactly(line1.getName(), line2.getName(), line3.getName());
    }
}
