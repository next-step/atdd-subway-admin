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
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Line line;
    Station 사당역;
    Station 교대역;
    Station 강남역;
    Station 선릉역;
    Station 삼성역;
    Station 잠실역;
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
        line.addInitSection(
            new Section.SectionBuilder()
            .line(line)
            .upStation(교대역)
            .downStation(삼성역)
            .distance(new Distance(10))
            .build()
        );
        lineRepository.save(line);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInTheMiddleOfUp() {
        setParam(교대역, 강남역, 4);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInTheMiddleOfDown() {
        setParam(선릉역, 삼성역, 4);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionNewUpStation() {
        setParam(사당역, 교대역, 4);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionNewDownStation() {
        setParam(삼성역, 잠실역, 4);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionInTheMiddleOfException() {
        setParam(교대역, 강남역, 10);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionAlreadyExistException() {
        setParam(교대역, 삼성역, 10);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionNotIncludeException() {
        setParam(강남역, 선릉역, 2);
        ExtractableResponse<Response> response = createStation();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void setParam(Station upStation, Station downStation, int distance) {
        params = new HashMap<>();
        params.put("upStationId", Long.toString(upStation.getId()));
        params.put("downStationId", Long.toString(downStation.getId()));
        params.put("distance", Integer.toString(distance));
    }

    private ExtractableResponse<Response> createStation() {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + line.getId() + "/sections")
            .then().log().all()
            .extract();
    }
}
