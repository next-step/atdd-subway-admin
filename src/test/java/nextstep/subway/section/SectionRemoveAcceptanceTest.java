package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.SectionAcceptanceTestRequest.*;
import static nextstep.subway.utils.SectionAcceptanceTestResponse.지하철_노선_구간_등록_예외_케이스로_등록되지_않음;
import static nextstep.subway.utils.SectionAcceptanceTestResponse.지하철_노선에_지하철역_제거됨;

@DisplayName("지하철 노선에 역 제거 관련 기능")
public class SectionRemoveAcceptanceTest extends AcceptanceTest {

    private Long 노선ID;
    private Long 봉천역ID;
    private Long 서울대입구역ID;
    private Long 낙성대역ID;
    private Long DEFAULT_DISTANCE;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 봉천역 = 지하철역_등록되어_있음("봉천역");
        ExtractableResponse<Response> 서울대입구역 = 지하철역_등록되어_있음("서울대입구역");
        ExtractableResponse<Response> 낙성대역 = 지하철역_등록되어_있음("낙성대역");

        봉천역ID = 봉천역.as(StationResponse.class).getId();
        서울대입구역ID = 서울대입구역.as(StationResponse.class).getId();
        낙성대역ID = 낙성대역.as(StationResponse.class).getId();

        DEFAULT_DISTANCE = 10L;
        LineRequest 노선_요청 = 노선_구간_요청_생성("2호선", "bg-green-600", 봉천역ID, 서울대입구역ID, DEFAULT_DISTANCE);
        ExtractableResponse<Response> 노선 = 노선_등록시_구간_등록되어_있음(노선_요청);

        노선ID = 노선.as(LineResponse.class).getId();
    }

    @DisplayName("노선의 구간을 제거할 수 있음: 종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void removeLastStation() {
        // given
        노선에_지하철역_등록_요청(노선ID, 서울대입구역ID, 낙성대역ID, DEFAULT_DISTANCE);

        // when
        ExtractableResponse<Response> response = 노선에_지하철역_제거_요청(노선ID, 낙성대역ID);

        // then
        지하철_노선에_지하철역_제거됨(response);
    }

    @DisplayName("노선에 등록되어있지 않은 역을 제거할 때 제거 할 수 없음")
    @Test
    void canNotRemoveUnregisteredStation() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_제거_요청(노선ID, 낙성대역ID);

        // then
        지하철_노선_구간_등록_예외_케이스로_등록되지_않음(response);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때 제거 할 수 없음")
    @Test
    void canNotRemoveStationInOneSection() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_제거_요청(노선ID, 서울대입구역ID);

        // then
        지하철_노선_구간_등록_예외_케이스로_등록되지_않음(response);
    }
}
