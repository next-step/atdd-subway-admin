package nextstep.subway.section;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 당산역;
    private LineResponse 이호선;

    @BeforeEach
    public void setUp(){
        super.setUp();
        강남역 = 지하철역_등록("강남역").as(StationResponse.class);
        당산역 = 지하철역_등록("당산역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 강남역.getId(), 당산역.getId(), 10);
        이호선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 추가한다 - 역사이에 새로운 역을 등록한다")
    @Test
    public void addSectionBetween(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록됨();
    }

    @DisplayName("노선에 구간을 추가한다 - 새로운 역을 상행 종점으로 등록한다")
    @Test
    public void addSectionFront(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록됨();
    }

    @DisplayName("노선에 구간을 추가한다 - 새로운 역을 하행 종점으로 등록한다")
    @Test
    public void addSectionEnd(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록됨();
    }

    @DisplayName("노선에 구간을 추가한다 - 역사이에 새로운역을 등록할때, 기존역 사이보다 크거나 같으면 등록할 수 없다")
    @Test
    public void addSectionBetween__DistanceLongerException(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록안됨();
    }

    @DisplayName("노선에 구간을 추가한다 - 상행역과 하행역이 이미 노선에 등록되어있으면 추가할 수 없다")
    @Test
    public void addSectionBoth__Conflict(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록안됨();
    }

    @DisplayName("노선에 구간을 추가한다 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    public void addSection__None(){
        //when
        지하철_노선에_지하철역_등록_요청();

        //then
        지하철_노선에_지하철역_등록안됨();
    }

    private void 지하철_노선에_지하철역_등록_요청(){}
    private void 지하철_노선에_지하철역_등록됨(){}
    private void 지하철_노선에_지하철역_등록안됨(){}
}
