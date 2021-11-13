package nextstep.subway.station.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private StationResponse 역삼역;
    private StationResponse 사당역;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        역삼역 = 지하철_역_등록되어_있음(new StationRequest("역삼역"));
        사당역 = 지하철_역_등록되어_있음(new StationRequest("사당역"));
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(stationRequest);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest(역삼역.getName());

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(stationRequest);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(response, Arrays.asList(역삼역, 사당역));
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(역삼역.getId());

        // then
        지하철_역_삭제됨(response);
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

    private void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, List<StationResponse> lineResponses) {
        List<Long> stationIds = new ArrayList<>(response.jsonPath().getList(".", StationResponse.class))
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        List<Long> expectedStationIds = lineResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsAll(expectedStationIds);
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
