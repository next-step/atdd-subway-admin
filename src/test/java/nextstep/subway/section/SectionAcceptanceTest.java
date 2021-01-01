package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineDataHelper;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.StationDataHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    StationDataHelper stationDataHelper;
    @Autowired
    LineDataHelper lineDataHelper;
    
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void saveSection() {
        // given
        Long 강남Id = stationDataHelper.역추가("강남역");
        Long 광교Id = stationDataHelper.역추가("광교역");
        Section section = new Section(강남Id, 광교Id, 10);
        Line savedLine = lineDataHelper.지하철_노선_추가(
                new Line("신분당선", "bg-red-600", new Sections(section))
        );

        // when
        Map<String, String> params = new HashMap<>();
        params.put("downStationId" ,"12");
        params.put("upStationId" ,"20");
        params.put("distance" ,"10");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + savedLine.getId() +"/sections")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정")
    @Test
    void saveSectionBetween() {
        // given
        Long 강남Id = stationDataHelper.역추가("강남역");
        Long 광교Id = stationDataHelper.역추가("광교역");
        Long 양재Id = stationDataHelper.역추가("양재역");
        Line savedLine = lineDataHelper.지하철_노선_추가(
                new Line("신분당선", "red", new Sections(new Section(강남Id, 광교Id, 10)))
        );

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId" , 강남Id.toString());
        params.put("downStationId" , 양재Id.toString());
        params.put("distance" ,"3");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + savedLine.getId() +"/sections")
                .then().log().all().extract();

        // then
        Optional<Line> found = lineDataHelper.지하철_노선_조회(savedLine.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(found.get().getAllIncludedStationIds()).containsExactly(강남Id, 양재Id, 광교Id);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void saveSectionAsNewUpStation() {
        // given
        Long 강남Id = stationDataHelper.역추가("강남역");
        Long 광교Id = stationDataHelper.역추가("광교역");
        Long 상행종점Id = stationDataHelper.역추가("상행종점");
        Line savedLine = lineDataHelper.지하철_노선_추가(
                new Line("신분당선", "red", new Sections(new Section(강남Id, 광교Id, 10)))
        );

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId" , 상행종점Id.toString());
        params.put("downStationId" , 강남Id.toString());
        params.put("distance" ,"3");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + savedLine.getId() +"/sections")
                .then().log().all().extract();

        // then
        Optional<Line> found = lineDataHelper.지하철_노선_조회(savedLine.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(found.get().getAllIncludedStationIds()).containsExactly(상행종점Id, 강남Id, 광교Id);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void saveSectionAsNewDownStation() {
        // given
        Long 강남Id = stationDataHelper.역추가("강남역");
        Long 광교Id = stationDataHelper.역추가("광교역");
        Long 하행종점Id = stationDataHelper.역추가("하행종점");
        Line savedLine = lineDataHelper.지하철_노선_추가(
                new Line("신분당선", "red", new Sections(new Section(강남Id, 광교Id, 10)))
        );

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId" , 광교Id.toString());
        params.put("downStationId" , 하행종점Id.toString());
        params.put("distance" ,"3");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + savedLine.getId() +"/sections")
                .then().log().all().extract();

        // then
        Optional<Line> found = lineDataHelper.지하철_노선_조회(savedLine.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(found.get().getAllIncludedStationIds()).containsExactly(강남Id, 광교Id, 하행종점Id);
    }
}
