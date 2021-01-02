package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.SectionAddAcceptanceTestRequest.*;
import static nextstep.subway.line.SectionRemoveAcceptanceTestRequest.*;
import static nextstep.subway.line.SectionRemoveAcceptanceTestResponse.*;

@DisplayName("지하철 노선에 역 제거 관련 기능")
public class SectionRemoveAcceptanceTest extends AcceptanceTest {
    private Long 노선ID;
    private Long 강남ID;
    private Long 역삼ID;
    private Long 선릉ID;
    private Long DEFAULT_DISTANCE;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 강남역 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> 역삼역 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> 선릉역 = 지하철역_등록되어_있음("선릉역");

        강남ID = 강남역.as(StationResponse.class).getId();
        역삼ID = 역삼역.as(StationResponse.class).getId();
        선릉ID = 선릉역.as(StationResponse.class).getId();

        DEFAULT_DISTANCE = 10L;
        LineRequest 노선_요청 = 노선_구간_요청_생성("2호선", "bg-green-600", 강남ID, 역삼ID, DEFAULT_DISTANCE);
        ExtractableResponse<Response> 노선 = 노선_등록시_구간_등록되어_있음(노선_요청);

        노선ID = 노선.as(LineResponse.class).getId();
    }

    @DisplayName("노선의 구간을 제거할 수 있음: 종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void removeLastStation() {
        // given
        노선에_지하철역_등록_요청(노선ID, 역삼ID, 선릉ID, DEFAULT_DISTANCE); // 강남 - 역삼 - 선릉

        // when
        ExtractableResponse<Response> response = 노선에_지하철역_제거_요청(노선ID, 선릉ID);

        // then
        지하철_노선에_지하철역_제거됨(response);
        노선에_지하철역_제거된_목록_정렬됨(response, Arrays.asList(강남ID, 역삼ID));
    }

    @DisplayName("노선의 구간을 제거할 수 있음: 중간역이 제거될 경우 재배치를 함")
    @Test
    void removeMiddleStation() {
        // given
        노선에_지하철역_등록_요청(노선ID, 역삼ID, 선릉ID, DEFAULT_DISTANCE); // 강남 - 역삼 - 선릉

        // when
        ExtractableResponse<Response> response = 노선에_지하철역_제거_요청(노선ID, 역삼ID);

        // then
        지하철_노선에_지하철역_제거됨(response);
        노선에_지하철역_제거된_목록_정렬됨(response, Arrays.asList(강남ID, 선릉ID));
    }
}
