package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> section;

    @BeforeEach
    void setting() {
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("강남역")); //1
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("역삼역")); //2
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("교대역")); //3
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("서울역")); //4

        section = LineAcceptanceTest.지하철_노선_생성(LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "1", "3", "7"));
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionBetween() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "4");

        // when
        // 역 사이에 새로운 역을 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_지하철역_등록됨
        LineAcceptanceTest.지하철_노선_생성_성공(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionBefore() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "2", "1", "4");

        // when
        // 새로운 역을 상행 종점으로 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_지하철역_등록됨
        LineAcceptanceTest.지하철_노선_생성_성공(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAfter() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "3", "2", "3");

        // when
        // 새로운 역을 하행 종점으로 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_지하철역_등록됨
        LineAcceptanceTest.지하철_노선_생성_성공(response);
    }


    @DisplayName("역 사이에 새로운 역을 등록할때, 거리가 기존보다 크거나 같으면 안된다.")
    @Test
    void addFarSectionBetween() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "7");

        // when
        // 역 사이에 거리가 더 먼 새로운 역을 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_구간_생성_실패됨
        LineAcceptanceTest.지하철_노선_생성_실패(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addExistsSection() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "1", "3", "7");

        // when
        // 존재하는 구간을 구간에 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_구간_생성_실패됨
        LineAcceptanceTest.지하철_노선_생성_실패(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addIrrelevantSection() {
        // given
        // 새로운 역 제공
        Map<String, String> params = LineAcceptanceTest.지하철_노선_종점_제공("2호선", "green darken-1", "2", "4", "7");

        // when
        // 새로운 역을 구간에 등록
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_생성(params);

        // then
        // 지하철_노선에_구간_생성_실패됨
        LineAcceptanceTest.지하철_노선_생성_실패(response);
    }

}
