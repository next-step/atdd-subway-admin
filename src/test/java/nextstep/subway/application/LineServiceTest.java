package nextstep.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
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
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, yangjae);
    }
}
