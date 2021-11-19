package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String API_URL = "/stations";

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        저장한다(params, API_URL);

        // when
        ExtractableResponse<Response> response = 저장한다(params, API_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 생성한다.")
    @Override
    @Test
    public void create() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 저장한다(params, API_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철역 한건 조회한다.")
    @Override
    @Test
    public void getOne() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        ExtractableResponse<Response> saved = givenData_저장한다(params1, API_URL);

        //when
        ExtractableResponse<Response> response = 조회한다(saved.header("Location"));

        //then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name").equals(params1.get("name"))).isTrue();
        });
    }

    @DisplayName("지하철역을 조회한다.")
    @Override
    @Test
    public void getList() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        List<ExtractableResponse<Response>> givenList = givenDataList_저장한다(
            new Object[]{params1, params2}, API_URL);

        // when
        ExtractableResponse<Response> response = 조회한다(API_URL);

        // then
        List<Long> expectedLineIds = getIdsByResponse(givenList, Long.class);
        List<Long> resultLineIds = response.jsonPath().getList("id", Long.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(resultLineIds).containsAll(expectedLineIds);
        });
    }

    @DisplayName("지하철역 이름을 수정한다.")
    @Override
    @Test
    public void update() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> givenData = givenData_저장한다(params, API_URL);

        //when
        String uri = givenData.header("Location");
        ExtractableResponse<Response> response = 수정한다(new StationRequest("강남구청역"), uri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Override
    @Test
    public void delete() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> givenData = givenData_저장한다(params, API_URL);

        // when
        String uri = givenData.header("Location");
        ExtractableResponse<Response> response = 삭제한다(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
