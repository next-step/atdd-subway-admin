package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.StationAcceptanceTestUtil.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> 지하철역등록응답 = 지하철됨_역_생성_됨("강남역");

        // then
        지하철역_응답_검증(지하철역등록응답, HttpStatus.CREATED);
        지하철_등록_성공(지하철역등록응답);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "역삼역";
        지하철됨_역_생성_됨(stationName);

        // when
        ExtractableResponse<Response> 지하철역등록응답 = 지하철됨_역_생성_됨(stationName);

        // then
        지하철역_응답_검증(지하철역등록응답, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Long 강남역ID = 지하철됨_역_생성_됨_toId("강남역");
        Long 역삼역ID = 지하철됨_역_생성_됨_toId("역삼역");

        // when
        ExtractableResponse<Response> 지하철역목록응답 = 지하철_역_목록_조회();

        // then
        지하철역_응답_검증(지하철역목록응답, HttpStatus.OK);
        지하철_역_목록_조회에_역ID_포함됨(Arrays.asList(강남역ID, 역삼역ID), 지하철역목록응답);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 지하철역등록응답 = 지하철됨_역_생성_됨("강남역");

        // when
        ExtractableResponse<Response> 지하철역제거응답 = 지하철_역_제거_함(지하철역등록응답);

        // then
        지하철역_응답_검증(지하철역제거응답, HttpStatus.NO_CONTENT);
    }

}
