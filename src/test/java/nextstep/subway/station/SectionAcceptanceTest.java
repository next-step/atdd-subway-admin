package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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
  private StationResponse 양재역;
  private StationResponse 광교역;
  private LineResponse 신분당선;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    // given
    강남역 = StationAcceptanceTest.지하철_등록_요청("강남역").as(StationResponse.class);
    양재역 = StationAcceptanceTest.지하철_등록_요청("양재역").as(StationResponse.class);
    광교역 = StationAcceptanceTest.지하철_등록_요청("광교역").as(StationResponse.class);

    LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
    신분당선 = LineAcceptanceTest.지하철_노선_생성_요청(lineRequest).as(LineResponse.class);
  }

  @DisplayName("노선에 구간을 등록 - 역 사이에 새로운 역을 등록할 경우(상행역이 같은 경우)")
  @Test
  void addSection1() {
    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남역", "양재역", "광교역");
  }

  @DisplayName("노선에 구간을 등록 - 역 사이에 새로운 역을 등록할 경우(하행역이 같은 경우)")
  @Test
  void addSection2() {
    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 양재역.getId(), 광교역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남역", "양재역", "광교역");
  }

  @DisplayName("노선에 구간을 등록 - 새로운 역을 상행 종점으로 등록할 경우")
  @Test
  void addSection3() {
    // when
    StationResponse 강남전역 = StationAcceptanceTest.지하철_등록_요청("강남전역").as(StationResponse.class); // 강남역 앞
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남전역.getId(), 강남역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남전역", "강남역", "광교역");
  }

  @DisplayName("노선에 구간을 등록 - 새로운 역을 하행 종점으로 등록할 경우")
  @Test
  void addSection4() {
    // when
    StationResponse 광교뒤역 = StationAcceptanceTest.지하철_등록_요청("광교뒤역").as(StationResponse.class); // 강남역 앞
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 광교역.getId(), 광교뒤역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남역", "광교역", "광교뒤역");
  }

  @DisplayName("노선에 구간 등록 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
  @Test
  void exceptionCase1() {
    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @DisplayName("노선에 구간 등록 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
  @Test
  void exceptionCase2() {
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @DisplayName("노선에 구간 등록 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
  @Test
  void exceptionCase3() {
    StationResponse 청계산입구역 = StationAcceptanceTest.지하철_등록_요청("청계산입구역").as(StationResponse.class);
    ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), 5);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }


  @DisplayName("노선에 구간을 삭제 - 역 앞 종점을 삭제한 경우")
  @Test
  void removeSection1() {
    // given
    지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철구간_제거_요청(신분당선.getId(), 강남역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = response.as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("양재역", "광교역");
  }

  @DisplayName("노선에 구간을 삭제 - 역 뒤 종점을 삭제한 경우")
  @Test
  void removeSection2() {
    // given
    지하철_노선에_지하철역_등록_요청(신분당선.getId(), 양재역.getId(), 광교역.getId(), 5);

    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철구간_제거_요청(신분당선.getId(), 광교역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = response.as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남역", "양재역");
  }

  @DisplayName("노선에 구간을 삭제 - ABC중 중간인 B를 삭제한 경우")
  @Test
  void removeSection3() {
    // given
    지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철구간_제거_요청(신분당선.getId(), 양재역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);
    assertThat(lineResponse.getStations()).extracting("name").containsExactly("강남역", "광교역");
  }

  @DisplayName("노선에 구간을 삭제 - 노선에 등록되어있지 않은 역을 제거하려 한다.")
  @Test
  void removeSectionExceptionCase1() {
    // given
    StationResponse 잠실역 = StationAcceptanceTest.지하철_등록_요청("잠실역").as(StationResponse.class);

    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철구간_제거_요청(신분당선.getId(), 잠실역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @DisplayName("노선에 구간을 삭제 - 구간이 하나인 노선에서 마지막 구간을 제거하려 한다.")
  @Test
  void removeSectionExceptionCase2() {
    // when
    ExtractableResponse<Response> response = 지하철_노선에_지하철구간_제거_요청(신분당선.getId(), 광교역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
    SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
    return RestAssured.given().log().all()
        .body(sectionRequest)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/lines/" + lineId + "/sections")
        .then().log().all()
        .extract();
  }

  private ExtractableResponse<Response> 지하철_노선에_지하철구간_제거_요청(Long lineId, Long stationId) {
    return RestAssured.given().log().all()
        .when()
        .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
        .then().log().all()
        .extract();
  }

}
