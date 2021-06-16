package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.station.StationStepTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    public static final StationRequest TEST_GANGNAM_STATION = new StationRequest("강남역");
    public static final StationRequest TEST_YUCKSAM_STATION = new StationRequest("역삼역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(TEST_GANGNAM_STATION);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(TEST_GANGNAM_STATION);

        //when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(TEST_GANGNAM_STATION);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        long gangnamId = 지하철_역_등록되어_있음(TEST_GANGNAM_STATION);
        long yucksamId = 지하철_역_등록되어_있음(TEST_YUCKSAM_STATION);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_조회_성공됨(response);
        지하철_역_목록_포함됨(response, gangnamId, yucksamId);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        long createdId = 지하철_역_등록되어_있음(TEST_GANGNAM_STATION);

        ExtractableResponse<Response> response = 지하철_역_삭제_요청(createdId);

        // then
        지하철_역_삭제됨(response);
    }
}
