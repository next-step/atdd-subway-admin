package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {
    public static final String path = "/stations";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        응답결과_확인(response, HttpStatus.CREATED);

        // then
        지하철역_존재함(전체_지하철역_이름_조회(), "강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        응답결과_확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성("지하철역이름");
        지하철역_생성("새로운지하철역이름");
        지하철역_생성("또다른지하철역이름");

        //when
        ExtractableResponse<Response> response = 전체_지하철역_조회();

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철역_개수_확인(response, 3);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        long id = 지하철역_생성_id_반환("강남역");

        // when
        지하철역_삭제(id);

        // then
        지하철역_존재하지_않음(전체_지하철역_이름_조회(), "강남역");
    }

    public static ExtractableResponse<Response> 전체_지하철역_조회() {
        return get(path);
    }

    public static List<String> 전체_지하철역_이름_조회() {
        return 전체_지하철역_조회()
                .jsonPath()
                .getList("name", String.class);
    }

    public static ExtractableResponse<Response> 지하철역_생성(String name) {
        return post(path, new StationRequest(name));
    }

    public static Long 지하철역_생성_id_반환(String name) {
        return 지하철역_생성(name)
                .jsonPath()
                .getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_삭제(long id) {
        return delete(path, id);
    }

    private void 지하철역_개수_확인(ExtractableResponse<Response> response, int size) {
        assertThat(response.jsonPath().getList(".").size()).isEqualTo(size);
    }

    private void 지하철역_존재함(List<String> stationNames, String searchName) {
        assertThat(stationNames).containsAnyOf(searchName);
    }

    private void 지하철역_존재하지_않음(List<String> stationNames, String searchName) {
        assertThat(stationNames.contains(searchName)).isFalse();
    }
}
