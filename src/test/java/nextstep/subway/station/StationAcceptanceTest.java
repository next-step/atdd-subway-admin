package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_역을_생성한다() {
        // when
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);

        // then
        지하철_역_생성됨(지하철_역_생성_요청_응답);
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        생성_요청(STATION_ROOT_PATH, 강남역);

        // when
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);

        // then
        지하철_역_생성_실패됨(지하철_역_생성_요청_응답);
    }

    @Test
    void 지하철역_목록을_조회한다() {
        /// given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        StationRequest 역삼역 = 지하철_역_파라미터_생성("역삼역");
        ExtractableResponse<Response> 강남역_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);
        ExtractableResponse<Response> 역삼역_응답 = 생성_요청(STATION_ROOT_PATH, 역삼역);

        // when
        ExtractableResponse<Response> 지하철_역_목록_조회_요청_응답 = 조회_요청(STATION_ROOT_PATH);

        // then
        지하철_역_목록_응답됨(지하철_역_목록_조회_요청_응답);
        List<Long> expectedLineIds = 지하철_역_IDs_추출(강남역_응답, 역삼역_응답);
        List<Long> resultLineIds = 지하철_역_목록_IDs_추출(지하철_역_목록_조회_요청_응답);
        지하철_역_목록_포함됨(expectedLineIds, resultLineIds);
    }

    @Test
    void 지하철역을_제거한다() {
        // given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);
        long 강남역_ID = 지하철_역_ID_추출(지하철_역_생성_요청_응답);

        // when
        ExtractableResponse<Response> 지하철_역_삭제_요청_응답 = 삭제_요청(STATION_ROOT_PATH + "/" + 강남역_ID);

        // then
        지하철_역_삭제됨(지하철_역_삭제_요청_응답);
    }

    public static StationResponse 지하철_역_생성(ExtractableResponse<Response> response) {
        return response.as(StationResponse.class);
    }

    public static List<Long> 지하철_역_목록_IDs_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @SafeVarargs
    public static List<Long> 지하철_역_IDs_추출(ExtractableResponse<Response>... createResponse) {
        return Arrays.stream(createResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    public static long 지하철_역_ID_추출(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    public static void 지하철_역_목록_포함됨(List<Long> expectedLineIds, List<Long> resultLineIds) {
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static StationRequest 지하철_역_파라미터_생성(String name) {
        return StationRequest.from(name);
    }

    public static void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
