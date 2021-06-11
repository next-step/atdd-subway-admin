package nextstep.subway.section;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionAcceptanceTest extends AcceptanceTest {

    private final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
    private final LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();

    private LineResponse line;

    @BeforeEach
    public void setUp() {
        super.setUp();

        stationAcceptanceTest.지하철_역_등록되어_있음("서울역");
        stationAcceptanceTest.지하철_역_등록되어_있음("회현역");
        stationAcceptanceTest.지하철_역_등록되어_있음("명동역");

        line = lineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("4호선", "bg-blue", 1L, 2L, 10))
        .as(LineResponse.class);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void 구간생성() {
        // when
        //구간_생성_요청

        // then
        //구간_생성_완료
    }

    @DisplayName("구간 등록 : 구간의 상행역이 상행종점")
    @Test
    void 구간등록_when_상행역이_상행종점() {
        // when
        //구간_생성_요청

        //구간을_노선에_등록_요청

        // then
        //구간_등록_완료
   }

    @DisplayName("구간 등록 : 구간의 하행역이 하행종점")
    @Test
    void 구간등록_when_하행역이_하행종점() {
        // when
        //구간_생성_요청

        //구간을_노선에_등록_요청

        // then
        //구간_등록_완료
    }

    @DisplayName("구간 등록 : 역 사이에 새로운 역 등록(기존 상행역에 연결)")
    @Test
    void 구간등록_when_상행역에_신규_역_연결() {
        // when
        //구간_생성_요청

        //구간을_노선에_등록_요청

        // then
        //구간_등록_완료
    }

    @DisplayName("구간 등록 : 역 사이에 새로운 역 등록(기존 하행역에 연결)")
    @Test
    void 구간등록_when_하행역에_신규_역_연결() {
        // when
        //구간_생성_요청

        //구간을_노선에_등록_요청

        // then
        //구간_등록_완료
    }

}
