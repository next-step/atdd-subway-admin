package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SectionRepository sectionRepository;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    void 지하철_노선_생성() {
        // given, when
        지하철_노선_생성("강남역", "논현역", "신분당선");

        // then
        List<String> lines = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
        assertThat(lines).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철_노선_목록_조회() {
        // given
        지하철_노선_생성("강남역", "논현역", "신분당선");
        지하철_노선_생성("선정릉", "선릉", "분당선");

        // when
        List<String> lines = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);

        // then
        assertThat(lines).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철_노선_조회() {
        // given
        Long id = 지하철_노선_생성("강남역", "논현역", "신분당선");

        // when
        String name = RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract().path("name");

        // then
        assertThat(name).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철_노선_수정() {
        // given
        Station upStation = 지하철_역_생성("강남역");
        Station downStation = 지하철_역_생성("논현역");
        Section section = 지하철_구간_생성(10, upStation, downStation);
        Long id = 지하철_노선_생성("신분당선", "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

        // when
        String name = "신분당손";
        String color = "무지개색";
        LineRequest lineRequest = new LineRequest(name, color, section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

        JsonPath jsonPath = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract().jsonPath();

        // then
        assertAll(
                () -> assertThat(jsonPath.getString("name")).isEqualTo(name),
                () -> assertThat(jsonPath.getString("color")).isEqualTo(color)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철_노선_삭제() {
        // given
        Long id = 지하철_노선_생성("강남역", "논현역", "신분당선");

        // when
        RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all();

        // then
        List<String> names = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
        assertThat(names).isEmpty();
    }

    private Section 지하철_구간_생성(int distance, Station upStation, Station downStation) {
        return sectionRepository.save(new Section(distance, upStation, downStation));
    }

    private Station 지하철_역_생성(String name) {
        return stationRepository.save(new Station(name));
    }

    private Long 지하철_노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return (long) (int) RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().statusCode(HttpStatus.CREATED.value()).log().all()
                .extract().path("id");
    }

    private Long 지하철_노선_생성(String upStationName, String downStationName, String lineName) {
        Station upStation = 지하철_역_생성(upStationName);
        Station downStation = 지하철_역_생성(downStationName);
        Section section = 지하철_구간_생성(10, upStation, downStation);
        return 지하철_노선_생성(lineName, "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
    }
}
