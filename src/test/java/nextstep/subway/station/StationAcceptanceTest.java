package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationAcceptanceFixture.지하철역_생성을_요청한다("강남역");

        // then
        같은_응답인지_확인한다(response, HttpStatus.CREATED);
        지하철역이_비어있는지_확인한다(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationAcceptanceFixture.지하철역_생성을_요청한다("강남역");

        // when
        ExtractableResponse<Response> response = StationAcceptanceFixture.지하철역_생성을_요청한다("강남역");

        // then
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = StationAcceptanceFixture.지하철역_생성을_요청한다("강남역");
        ExtractableResponse<Response> createResponse2 = StationAcceptanceFixture.지하철역_생성을_요청한다("역삼역");

        // when
        ExtractableResponse<Response> response = StationAcceptanceFixture.지하철역_목록을_조회한다();

        // then
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 같은_지하철역들_확인
        같은_지하철역들을_가지고있는지_확인한다(response, 지하철역_ID들로_변환한다(createResponse1, createResponse2));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = StationAcceptanceFixture.지하철역_생성을_요청한다("강남역");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = StationAcceptanceFixture.지하철역_삭제를_요청한다(uri);

        // then
        같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
    }

    private void 같은_응답인지_확인한다(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private void 지하철역이_비어있는지_확인한다(ExtractableResponse<Response> response) {
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 같은_지하철역들을_가지고있는지_확인한다(ExtractableResponse<Response> response, List<Long> expectedLineIds) {
        List<Long> resultLineIds = StationAcceptanceFixture.ofStationResponseIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static StationResponse 지하철역을_생성한다(String name) {
        return StationAcceptanceFixture.ofStationResponse(StationAcceptanceFixture.지하철역_생성을_요청한다(name));
    }

    private List<Long> 지하철역_ID들로_변환한다(ExtractableResponse<Response>... createResponses) {
        return Arrays.stream(createResponses)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }
}
