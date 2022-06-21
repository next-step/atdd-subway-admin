package nextstep.subway.acceptance;

import static nextstep.subway.utils.AssertionsUtils.assertBadRequest;
import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertNoContent;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getIdAsLong;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_목록_조회_요청;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_삭제_요청;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nextstep.subway.config.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // When
        Response stationCreateResponse = 지하철역_생성_요청("강남역");

        // Then
        Response getAllStationsResponse = 지하철역_목록_조회_요청();
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertCreated(stationCreateResponse),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 시 생성한 지하철역 이름 포함 여부 검증")
                .containsAnyOf("강남역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("이미 존재하는 지하철역 이름으로 지하철역을 생성할 수 없다")
    @Test
    void createStationWithDuplicateName() {
        // given
        Response creationResponse = 지하철역_생성_요청("판교역");

        // when
        Response duplicationCreateResponse = 지하철역_생성_요청("판교역");

        // then
        assertAll(
            () -> assertCreated(creationResponse),
            () -> assertBadRequest(duplicationCreateResponse)
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // Given
        지하철역_생성_요청("논현역");
        지하철역_생성_요청("신논현역");

        // When
        Response getAllStationsResponse = 지하철역_목록_조회_요청();

        // Then
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 응답에 생성한 두개의 지하철역 이름 포함 여부 검증")
                .containsAnyOf("논현역", "신논현역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // Given
        Long 선릉역 = getIdAsLong(지하철역_생성_요청("선릉역"));

        // When
        Response deleteResponse = 지하철역_삭제_요청(선릉역);

        // Then
        Response getAllStationsResponse = 지하철역_목록_조회_요청();
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertNoContent(deleteResponse),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 응답에 삭제한 지하철역 미포함 여부 검증")
                .doesNotContain("선릉역")
        );
    }
}
