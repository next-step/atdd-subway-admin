package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.StationTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = StationTestUtil.지하철됨_역_생성_됨("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "역삼역";
        StationTestUtil.지하철됨_역_생성_됨(stationName);
        Map<String, String> duplicateParams = StationTestUtil.지하철_역_생성_파라미터_맵핑(stationName);

        // when
        ExtractableResponse<Response> response = StationTestUtil.지하철_역_생성_요청(duplicateParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = StationTestUtil.지하철됨_역_생성_됨("강남역");
        ExtractableResponse<Response> createResponse2 = StationTestUtil.지하철됨_역_생성_됨("역삼역");

        // when
        ExtractableResponse<Response> response = StationTestUtil.지하철_역_목록_조회();

        // then
        List<Long> expectedLineIds = StationTestUtil.ids_추출_By_Location(createResponse1,
            createResponse2);
        List<Long> resultLineIds = StationTestUtil.ids_추출_By_StationResponse(response);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = StationTestUtil.지하철됨_역_생성_됨("강남역");
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationTestUtil.지하철_역_제거_함(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
