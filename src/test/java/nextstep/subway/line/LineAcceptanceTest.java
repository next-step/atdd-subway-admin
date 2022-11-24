package nextstep.subway.line;

import io.restassured.RestAssured;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    void 지하철_노선_생성() {
        // given
        Station upStation = createStation("강남역");
        Station downStation = createStation("논현역");
        Section section = createSection(10, upStation, downStation);

        // when
        createLine("신분당선", "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

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
        Station upStation = createStation("강남역");
        Station downStation = createStation("논현역");
        Section section = createSection(10, upStation, downStation);
        createLine("신분당선", "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

        upStation = createStation("선정릉");
        downStation = createStation("선릉");
        section = createSection(10, upStation, downStation);
        createLine("분당선", "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

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
        Station upStation = createStation("강남역");
        Station downStation = createStation("논현역");
        Section section = createSection(10, upStation, downStation);
        Long id = createLine("신분당선", "bg-red-600", section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());

        // when
        String name = RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract().path("name");

        // then
        assertThat(name).isEqualTo("신분당선");
    }

    private Section createSection(int distance, Station upStation, Station downStation) {
        return sectionRepository.save(new Section(distance, upStation, downStation));
    }

    private Station createStation(String name) {
        return stationRepository.save(new Station(name));
    }

    private Long createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return (long) (int) RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().statusCode(HttpStatus.CREATED.value()).log().all()
                .extract().path("id");
    }
}
