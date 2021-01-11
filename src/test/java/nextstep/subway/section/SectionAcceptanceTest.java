package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Station gangnam;
    Station seocho;
    Station gyodae;

    Line line;

    Map<String, String> params;

    @BeforeEach
    public void setUp() {
        super.setUp();
        line = new Line("2호선", "green");
        gangnam = stationRepository.save(new Station("강남역"));
        seocho = stationRepository.save(new Station("서초역"));
        gyodae = stationRepository.save(new Station("교대역"));
        Section section = new Section(line, gangnam, seocho, 10);
        line.addSection(section);
        lineRepository.save(line);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInTheMiddleOf() {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(gangnam.getId()));
        params.put("downStationId", Long.toString(gyodae.getId()));
        params.put("distance", Integer.toString(4));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + line.getId() + "/sections")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
