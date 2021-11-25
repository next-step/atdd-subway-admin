package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.utils.StationAcceptanceTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> 지하철역_등록_응답 = StationAcceptanceTestUtil.지하철됨_역_생성_됨("강남역");

        // then
        assertAll(
            () -> assertThat(지하철역_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(지하철역_등록_응답.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "역삼역";
        StationAcceptanceTestUtil.지하철됨_역_생성_됨(stationName);

        // when
        ExtractableResponse<Response> 지하철역_등록_응답 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            stationName);

        // then
        assertThat(지하철역_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> 지하철역_등록_응답1 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "강남역");
        ExtractableResponse<Response> 지하철역_등록_응답2 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "역삼역");

        // when
        ExtractableResponse<Response> 지하철역_목록_응답 = StationAcceptanceTestUtil.지하철_역_목록_조회();

        // then
        List<Long> 생성된_지하철역_예상_ID_목록 = StationAcceptanceTestUtil.ids_추출_By_Location(
            Arrays.asList(지하철역_등록_응답1, 지하철역_등록_응답2));
        List<Long> 지하철역_목록_조회_지하철역_ID_목록 = StationAcceptanceTestUtil.ids_추출_By_StationResponse(
            지하철역_목록_응답);
        assertAll(
            () -> assertThat(지하철역_목록_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(지하철역_목록_조회_지하철역_ID_목록).containsAll(생성된_지하철역_예상_ID_목록)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 지하철역_등록_응답 = StationAcceptanceTestUtil.지하철됨_역_생성_됨("강남역");

        // when
        ExtractableResponse<Response> 지하철역_제거_응답 = StationAcceptanceTestUtil.지하철_역_제거_함(
            지하철역_등록_응답);

        // then
        assertThat(지하철역_제거_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
