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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineServiceTest extends AcceptanceTest {

    @Autowired
    LineService lineService;

    private long 강남역;
    private long 양재역;
    private long 판교역;
    private long 양재시민의숲;
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
        양재시민의숲 = StationAcceptanceTest.지하철역_등록되어_있음("양재시민의숲");

        // 지하철_노선_생성_요청
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 7);
    }

    @Transactional
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void betweenStationRegister() {
        lineService.addSection(신분당선, new SectionRequest(강남역, 판교역, 6));
        Line byId = lineService.findById(신분당선);

        LineResponse of = LineResponse.of(byId);
        assertThat(of.getStations().get(0).getId()).isEqualTo(강남역);
        assertThat(of.getStations().get(1).getId()).isEqualTo(판교역);
        assertThat(of.getStations().get(2).getId()).isEqualTo(양재역);
    }

    @Transactional
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void sameUpStationRegister() {
        lineService.addSection(신분당선, new SectionRequest(판교역, 강남역, 10));
        Line byId = lineService.findById(신분당선);

        LineResponse of = LineResponse.of(byId);
        assertThat(of.getStations().get(0).getId()).isEqualTo(판교역);
        assertThat(of.getStations().get(1).getId()).isEqualTo(강남역);
        assertThat(of.getStations().get(2).getId()).isEqualTo(양재역);
    }

    @Transactional
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void sameDownStationRegister() {
        lineService.addSection(신분당선, new SectionRequest(양재역, 판교역, 10));
        lineService.addSection(신분당선, new SectionRequest(판교역, 양재시민의숲, 10));
        Line byId = lineService.findById(신분당선);

        LineResponse of = LineResponse.of(byId);
        assertThat(of.getStations().get(0).getId()).isEqualTo(강남역);
        assertThat(of.getStations().get(1).getId()).isEqualTo(양재역);
        assertThat(of.getStations().get(2).getId()).isEqualTo(판교역);
        assertThat(of.getStations().get(3).getId()).isEqualTo(양재시민의숲);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addBetweenSectionExpectedException() {
        assertThatThrownBy(() -> lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 7)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addExistSectionExpectedException() {
        assertThatThrownBy(() -> {
            lineService.addSection(신분당선, new SectionRequest(강남역, 판교역, 6));
            //등록할 수 없음
            lineService.addSection(신분당선, new SectionRequest(판교역, 양재역, 6));
            lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 6));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addNotIncludeSectionExpectedException() {
        assertThatThrownBy(() -> lineService.addSection(신분당선, new SectionRequest(판교역, 양재시민의숲, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
