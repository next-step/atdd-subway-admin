package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RequestUtil;
import nextstep.subway.utils.StatusCodeCheckUtil;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    public static final List<String> EXPECTED_FIELDS =
        Arrays.asList("id", "name", "createdDate", "modifiedDate");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성됨(response);
        기대되는_모든_필드가_있는지_검증(response, ".");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        final ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        final ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청("강남역");
        final ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청("역삼역");

        // when
        final ExtractableResponse<Response> response = 지하철역_목록_조회();

        // then
        StatusCodeCheckUtil.ok(response);
        지하철역_리스트_필드_검증(createResponse1, createResponse2, response);
        기대되는_모든_필드가_있는지_검증(response, "[0]");
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
        final ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse);

        // then
        StatusCodeCheckUtil.noContent(response);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return RequestUtil.post("/stations", params);
    }

    private ExtractableResponse<Response> 지하철역_목록_조회() {
        return RequestUtil.get("/stations");
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(final ExtractableResponse<Response> response) {
        final String uri = response.header("Location");
        return RequestUtil.delete(uri);
    }

    private void 지하철역_생성됨(final ExtractableResponse<Response> response) {
        StatusCodeCheckUtil.created(response);
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철역_리스트_필드_검증(final ExtractableResponse<Response> createResponse1,
        final ExtractableResponse<Response> createResponse2,
        final ExtractableResponse<Response> response
    ) {
        final List<StationResponse> expectedStations = Stream.of(createResponse1, createResponse2)
            .map(it -> it.as(StationResponse.class))
            .collect(Collectors.toList());
        final List<StationResponse> actualStations = response.jsonPath().getList(".", StationResponse.class);

        assertThat(actualStations).containsAll(expectedStations);
    }

    private void 기대되는_모든_필드가_있는지_검증(final ExtractableResponse<Response> response, final String targetPath) {
        final Set<String> actualFields = response.jsonPath().getMap(targetPath, String.class, String.class).keySet();
        assertThat(actualFields).containsAll(EXPECTED_FIELDS);
    }
}
