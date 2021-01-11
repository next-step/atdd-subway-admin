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

    Station 사당역;
    Station 교대역;
    Station 강남역;
    Station 선릉역;
    Station 삼성역;
    Station 잠실역;

    Line line;

    Map<String, String> params;

    @BeforeEach
    public void setUp() {
        super.setUp();
        line = new Line("2호선", "green");
        사당역 = stationRepository.save(new Station("사당역"));
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        잠실역 = stationRepository.save(new Station("잠실역"));
        Section section = new Section(line, 교대역, 삼성역, 10);
        line.addSection(section);
        lineRepository.save(line);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInTheMiddleOfUp() {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(교대역.getId()));
        params.put("downStationId", Long.toString(강남역.getId()));
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

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInTheMiddleOfDown() {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(선릉역.getId()));
        params.put("downStationId", Long.toString(삼성역.getId()));
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

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionNewUpStation() {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(사당역.getId()));
        params.put("downStationId", Long.toString(교대역.getId()));
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

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionNewDownStation() {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(삼성역.getId()));
        params.put("downStationId", Long.toString(잠실역.getId()));
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

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionInTheMiddleOfException() {

    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionAlreadyExistException() {

    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionNotIncludeException() {

    }
}
