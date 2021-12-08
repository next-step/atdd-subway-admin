package nextstep.subway.station;

import static nextstep.subway.station.StationTestUtil.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 강남역 = 지하철역_정보("강남역");

        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성됨(지하철역_생성_응답);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 강남역 = 지하철역_정보("강남역");
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> 지하철역_중복_생성_응답 = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성_실패됨(지하철역_중복_생성_응답);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역 = 지하철역_정보("강남역");
        StationRequest 역삼역 = 지하철역_정보("역삼역");
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청(강남역);
        ExtractableResponse<Response> 역삼역_생성_응답 = 지하철역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> 지하철역_목록_조회_응답 = 지하철역_목록_조회_요청();

        // then
        지하철역_목록_조회됨(지하철역_목록_조회_응답, 강남역_생성_응답, 역삼역_생성_응답);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest 강남역 = 지하철역_정보("강남역");
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청(강남역);
        Long 강남역_생성_아이디 = 강남역_생성_응답.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 강남역_삭제_응답 = 지하철역_제거_요청(강남역_생성_아이디);

        // then
        지하철역_제거됨(강남역_삭제_응답);
    }

}
