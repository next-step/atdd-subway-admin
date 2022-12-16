package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("존재하지 않는 지하철역을 생성하면, 지하철역 목록 조회시 생성한 역을 찾을 수 있다.")
    @Test
    void createStation() {
        assertThat(StationAcceptanceTestHelper.createStation("강남역").statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(StationAcceptanceTestHelper.getStations().getList("name", String.class))
            .containsExactly("강남역");
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역 생성은 불가능 하다.")
    @Test
    void createStationWithDuplicateName() {
        assertThat(StationAcceptanceTestHelper.createStation("강남역").statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(StationAcceptanceTestHelper.createStation("강남역").statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 생성 후 목록을 조회하면, 생성한 지하철역을 응답 받을 수 있다.")
    @Test
    void getStations() {
        assertThat(StationAcceptanceTestHelper.createStation("강남역").statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(StationAcceptanceTestHelper.createStation("판교역").statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(StationAcceptanceTestHelper.getStations().getList("name", String.class))
            .containsExactlyInAnyOrder("강남역", "판교역");
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 생성 후 해당 지하철역을 삭제하면, 지하철역 목록 조회 시 삭제한 역은 조회 되지 않아야 한다.")
    @Test
    void deleteStation() {
        // given
        final String targetId = StationAcceptanceTestHelper.createStation("강남역")
            .jsonPath().getString("id");
        final String nonTargetId = StationAcceptanceTestHelper.createStation("판교역")
            .jsonPath().getString("id");
        assertThat(StationAcceptanceTestHelper.getStations().getList("id", String.class))
            .containsExactlyInAnyOrder(targetId, nonTargetId);
        // when
        RestAssured.given().log().all()
            .when().delete("/stations/{id}", targetId)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
        // then
        assertThat(StationAcceptanceTestHelper.getStations().getList("id", String.class))
            .containsExactly(nonTargetId);
    }
}
