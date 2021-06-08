package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final Map<String, String> 강남역 = new HashMap<>();
    private static final Map<String, String> 역삼역 = new HashMap<>();

    static {
        강남역.put("name", "강남역");
        역삼역.put("name", "역삼역");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 생성된_강남역 = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성됨(생성된_강남역);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> 생성된_강남역 = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성_실패됨(생성된_강남역);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 생성된_강남역 = 지하철_역_생성_요청(강남역);
        ExtractableResponse<Response> 생성된_역삼역 = 지하철_역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> 조회된_역_목록 = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(조회된_역_목록);
        지하철_역_목록_포함됨(생성된_강남역, 생성된_역삼역, 조회된_역_목록);
    }


    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 생성된_강남역 = 지하철_역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> 제거된_역 = 지하철_역_삭제_요청(생성된_강남역);

        // then
        지하철_역_삭제됨(제거된_역);
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록_포함됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2,
                              ExtractableResponse<Response> response) {

        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
