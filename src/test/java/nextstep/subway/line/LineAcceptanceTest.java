package nextstep.subway.line;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Station;
import nextstep.subway.fixture.LineTestFixture;
import nextstep.subway.fixture.StationTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // When
        Station 강남역 = StationTestFixture.create("강남역").extract().as(Station.class);
        Station 양재역 = StationTestFixture.create("양재역").extract().as(Station.class);
        long lineId = LineTestFixture.create("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // Then
        JsonPath responseBody = LineTestFixture.fetchAll().extract().jsonPath();
        assertThat(responseBody.getList("id", Long.class)).containsAnyOf(lineId);
        assertThat(responseBody.getList("stations[0].name")).containsExactly(강남역.getName(), 양재역.getName());
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // Given
        Station 강남역 = StationTestFixture.create("강남역").extract().as(Station.class);
        Station 양재역 = StationTestFixture.create("양재역").extract().as(Station.class);
        LineTestFixture.create("신분당선", "bg-red-600", 강남역, 양재역, 10).extract();

        Station 야탑역 = StationTestFixture.create("야탑역").extract().as(Station.class);
        Station 모란역 = StationTestFixture.create("모란역").extract().as(Station.class);
        LineTestFixture.create("분당선", "bg-yellow-600", 야탑역, 모란역, 10).extract();

        // When
        JsonPath responseBody = LineTestFixture.fetchAll().extract().jsonPath();

        // Then
        assertThat(responseBody.getList("").size()).isEqualTo(2);
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // Given
        Station 강남역 = StationTestFixture.create("강남역").extract().as(Station.class);
        Station 양재역 = StationTestFixture.create("양재역").extract().as(Station.class);
        String lineName = "신분당선";
        long lineId = LineTestFixture.create(lineName, "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        JsonPath responseBody = LineTestFixture.fetch(lineId).extract().jsonPath();

        // Then
        assertThat(responseBody.getLong("id")).isEqualTo(lineId);
        assertThat(responseBody.getString("name")).isEqualTo(lineName);
        assertThat(responseBody.getList("stations.name")).containsExactly(강남역.getName(), 양재역.getName());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // Given
        Station 강남역 = StationTestFixture.create("강남역").extract().as(Station.class);
        Station 양재역 = StationTestFixture.create("양재역").extract().as(Station.class);
        long lineId = LineTestFixture.create("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        ValidatableResponse response = LineTestFixture.update(lineId, "구분당선", "bg-blue-600");

        // Then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // Given
        Station 강남역 = StationTestFixture.create("강남역").extract().as(Station.class);
        Station 양재역 = StationTestFixture.create("양재역").extract().as(Station.class);
        long lineId = LineTestFixture.create("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        ValidatableResponse response = LineTestFixture.delete(lineId);

        // Then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
