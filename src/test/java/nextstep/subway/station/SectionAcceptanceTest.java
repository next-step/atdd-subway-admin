package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

  private StationResponse 강남역;
  private StationResponse 광교역;
  private LineResponse 신분당선;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    // given
    강남역 = StationAcceptanceTest.지하철_등록_요청("강남역").as(StationResponse.class);
    광교역 = StationAcceptanceTest.지하철_등록_요청("광교역").as(StationResponse.class);

    Map<String, String> createParams = new HashMap<>();
    createParams.put("name", "신분당선");
    createParams.put("color", "bg-red-600");
    createParams.put("upStationId", 강남역.getId() + "");
    createParams.put("downStationId", 광교역.getId() + "");
    createParams.put("distance", 10 + "");
    신분당선 = LineAcceptanceTest.지하철_노선_생성_요청(createParams).as(LineResponse.class);
  }

  @DisplayName("노선에 구간을 등록 - 역 사이에 새로운 역을 등록할 경우")
  @Test
  void addSection1() {
    // when
    StationResponse 양재역 = StationAcceptanceTest.지하철_등록_요청("양재역").as(StationResponse.class);
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
    assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("양재역");
    assertThat(lineResponse.getStations().get(2).getName()).isEqualTo("광교역");
  }

  @DisplayName("노선에 구간을 등록 - 새로운 역을 상행 종점으로 등록할 경우")
  @Test
  void addSection2() {
    // when
    StationResponse 강남전역 = StationAcceptanceTest.지하철_등록_요청("강남앞역").as(StationResponse.class); // 강남역 앞
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남전역.getId(), 강남역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남전역");
    assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("강남역");
    assertThat(lineResponse.getStations().get(2).getName()).isEqualTo("광교역");
  }

  @DisplayName("노선에 구간을 등록 - 새로운 역을 하행 종점으로 등록할 경우")
  @Test
  void addSection3() {
    // when
    StationResponse 광교뒤역 = StationAcceptanceTest.지하철_등록_요청("광교뒤역").as(StationResponse.class); // 강남역 앞
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 광교역.getId(), 광교뒤역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
    assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("광교역");
    assertThat(lineResponse.getStations().get(2).getName()).isEqualTo("광교뒤역");
  }

  private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
    Map<String, String> param = new HashMap<>();
    param.put("upStationId", upStationId + "");
    param.put("downStationId", downStationId + "");
    param.put("distance", distance + "");
    return RestAssured.given().log().all()
        .body(param)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/lines/" + lineId + "/sections")
        .then().log().all()
        .extract();
  }

}
