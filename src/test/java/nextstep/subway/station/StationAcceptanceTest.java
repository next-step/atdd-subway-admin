package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.enums.ErrorCode;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        // 지하철역_생성
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        // 지하철역_생성됨
        지하철역_생성됨(response);
    }

    public static ExtractableResponse<Response> 지하철역_생성(final String stationName) {
        return RestAssuredUtils.post(new StationRequest(stationName).toStation(), "/stations").extract();
    }

    public static ExtractableResponse<Response> 지하철역_생성_및_검증(final String stationName) {
        final ExtractableResponse<Response> createResponse = 지하철역_생성(stationName);

        지하철역_생성됨(createResponse);

        return createResponse;
    }

    public static void 지하철역_생성됨(ExtractableResponse<Response> createResponse) {
        assertAll(
            () -> 응답코드_검증(createResponse, HttpStatus.SC_CREATED),
            () -> assertThat(로케이션_가져오기(createResponse)).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        // 지하철역_등록되어_있음
        final String stationName = "강남역";
        지하철역_생성_및_검증(stationName);

        // when
        // 지하철역_생성
        final ExtractableResponse<Response> duplicatedResponse = 지하철역_생성(stationName);

        // then
        // 존재하는_이름으로_지하철역을_생성할수_없음
        존재하는_이름으로_지하철역을_생성할수_없음(duplicatedResponse);
    }

    private void 존재하는_이름으로_지하철역을_생성할수_없음(ExtractableResponse<Response> response) {
        assertAll(
            () -> 응답코드_검증(response, HttpStatus.SC_BAD_REQUEST),
            () -> 예외발생_검증(response, ErrorCode.LINE_NAME_ALREADY_EXISTS)
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        // 지하철역_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철역_생성_및_검증("강남역");

        // 지하철역_등록되어_있음
        final ExtractableResponse<Response> otherCreateResponse = 지하철역_생성_및_검증("역삼역");

        // when
        // 지하철역_목록_조회
        ExtractableResponse<Response> response = 지하철역_목록_조회();

        // then
        // 지하철역_목록_응답됨
        지하철역_목록_응답됨(response);

        // 지하철역_목록_포함됨
        지하철역_목록_포함됨(createResponse, otherCreateResponse, response);
    }

    private void 지하철역_목록_포함됨(ExtractableResponse<Response> createResponse,
        ExtractableResponse<Response> otherCreateResponse, ExtractableResponse<Response> response) {
        final List<Long> expectedLineIds = Stream.of(createResponse, otherCreateResponse)
            .map(AcceptanceTest::아이디_추출하기)
            .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssuredUtils.get("/stations").extract();
    }

    public static void 지하철역_목록_응답됨(ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_OK);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        // 지하철역_등록되어_있음
        final ExtractableResponse<Response> createResponse = 지하철역_생성_및_검증("강남역");

        // when
        // 지하철역_제거
        final String uri = 로케이션_가져오기(createResponse);
        final ExtractableResponse<Response> response = 지하철역_제거(uri);

        // then
        // 지하철역_제거됨
        지하철역_제거됨(response);
    }

    public static ExtractableResponse<Response> 지하철역_제거(String uri) {
        return RestAssuredUtils.delete(uri).extract();
    }

    public static void 지하철역_제거됨(ExtractableResponse<Response> response) {
        응답코드_검증(response, HttpStatus.SC_NO_CONTENT);
    }
}
