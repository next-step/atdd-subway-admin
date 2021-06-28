package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.AcceptanceTestSnippet.HTTP_응답_코드_확인;
import static nextstep.subway.station.ui.StationControllerTestSnippet.*;
import static nextstep.subway.utils.ExtractableResponseUtil.여러_응답에서_ID_추출;
import static nextstep.subway.utils.ExtractableResponseUtil.응답에서_ID_추출;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(new StationRequest("강남역"));

        // then
        HTTP_응답_코드_확인(response, CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_생성_요청(new StationRequest("강남역"));

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(new StationRequest("강남역"));

        // then
        HTTP_응답_코드_확인(response, BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성_요청(new StationRequest("강남역"));

        ExtractableResponse<Response> createResponse2 = 지하철_역_생성_요청(new StationRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        HTTP_응답_코드_확인(response, OK);
        List<Long> expectedLineIds = 여러_응답에서_ID_추출(createResponse1, createResponse2);
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청(new StationRequest("강남역"));

        // when
        Long id = 응답에서_ID_추출(createResponse);
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(id);

        // then
        HTTP_응답_코드_확인(response, NO_CONTENT);
    }
}
