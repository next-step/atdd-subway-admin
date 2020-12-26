package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

public class LineServiceTest extends AcceptanceTest {

    @Autowired
    LineService lineService;

    private long 강남역;
    private long 양재역;
    private long 판교역;
    private long 신분당선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // 지하철_역_생성_요청
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역");
        판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");

        // 지하철_노선_생성_요청
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역);
    }

    @Transactional
    @DisplayName("")
    @Test
    void name() {
        lineService.addSection(신분당선, new SectionRequest(판교역, 강남역, 10));
        Line byId = lineService.findById(신분당선);

        //강남역 -> 양재역 -> 판교역
        LineResponse of = LineResponse.of(byId);
        assertThat(of.getStations().get(0).getId()).isEqualTo(판교역);
        assertThat(of.getStations().get(1).getId()).isEqualTo(강남역);
        assertThat(of.getStations().get(2).getId()).isEqualTo(양재역);
    }
}
