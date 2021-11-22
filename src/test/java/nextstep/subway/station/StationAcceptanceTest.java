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
import org.apiguardian.api.API;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String API_URL = "/stations";

    private Map<String, String> params;

    @BeforeEach
    public void setUp() {
        super.setUp();
        params = new HashMap<>();
        params.put("name", "강남역");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        givenData_강남역_저장한다();

        // when
        ExtractableResponse<Response> response = 저장한다(params, API_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    public void create() {
        // when
        ExtractableResponse<Response> response = 저장한다(params, API_URL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철역 한건 조회한다.")
    @Test
    public void getOne() {
        // given
        ExtractableResponse<Response> saved = givenData_강남역_저장한다();

        //when
        ExtractableResponse<Response> response = 조회한다(saved.header("Location"));

        //then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name").equals("강남역")).isTrue();
        });
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    public void getList() {
        // given
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        List<ExtractableResponse<Response>> givenList = givenDataList_저장한다(
            new Object[]{params, params2}, API_URL);

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
    @Test
    public void update() {
        // given
        ExtractableResponse<Response> givenData = givenData_강남역_저장한다();

        //when
        String uri = givenData.header("Location");
        ExtractableResponse<Response> response = 수정한다(new StationRequest("강남구청역"), uri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    public void delete() {
        // given
        ExtractableResponse<Response> givenData = givenData_강남역_저장한다();

        // when
        String uri = givenData.header("Location");
        ExtractableResponse<Response> response = 삭제한다(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> givenData_강남역_저장한다() {
        return givenData_저장한다(params);
    }

    private ExtractableResponse<Response> givenData_저장한다(Map<String, String> params) {
        System.out.println("=== " + params);
        return 저장한다(params, API_URL);
    }
}
