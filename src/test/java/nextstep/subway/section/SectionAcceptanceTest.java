package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.등록된_역_ID;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

  private Long 압구정역_ID;

  private Long 강남역_ID;

  private Long 판교역_ID;

  private Long 광교역_ID;

  private Long 동탄역_ID;

  private LineResponse 신분당선;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    압구정역_ID = 등록된_역_ID(지하철_역_등록되어_있음("압구정역"));
    강남역_ID = 등록된_역_ID(지하철_역_등록되어_있음("강남역"));
    판교역_ID = 등록된_역_ID(지하철_역_등록되어_있음("판교역"));
    광교역_ID = 등록된_역_ID(지하철_역_등록되어_있음("광교역"));
    동탄역_ID = 등록된_역_ID(지하철_역_등록되어_있음("동탄역"));
    String toCreateName = "신분당선";
    String toCreateColor = "red";
    LineRequest params = LineRequest.toCreateRequestParameter(toCreateName, toCreateColor, 강남역_ID, 광교역_ID, 10);
    신분당선 = 지하철_노선_등록되어_있음(params).as(LineResponse.class);
  }


  @DisplayName("지하철 노선 중간에 구간 등록한다.")
  @Test
  void addSectionInMiddle() {
    // given
    SectionRequest sectionParam = new SectionRequest(강남역_ID, 판교역_ID, 5);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록됨(result, lineId, 강남역_ID, 판교역_ID, 광교역_ID);
  }

  @DisplayName("새로운 역을 상행 종점으로 등록한다.")
  @Test
  void adSectionInHead() {
    // given
    SectionRequest sectionParam = new SectionRequest(압구정역_ID, 강남역_ID, 5);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록됨(result, lineId, 압구정역_ID, 강남역_ID, 광교역_ID);
  }

  @DisplayName("새로운 역을 하행 종점으로 등록한다.")
  @Test
  void addSectionInTail() {
    // given
    SectionRequest sectionParam = new SectionRequest(광교역_ID, 동탄역_ID, 5);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록됨(result, lineId, 강남역_ID, 광교역_ID, 동탄역_ID);
  }

  @DisplayName("기존 역 사이 길이보다 새로운 구간의 길이가 크면 등록할 수 없다.")
  @Test
  void tooLongDistanceTest() {
    // given
    SectionRequest sectionParam = new SectionRequest(강남역_ID, 판교역_ID, 20);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록_실패함(result);
  }

  @DisplayName("기존 역 사이 길이보다 새로운 구간의 길이가 같으면 등록할 수 없다.")
  @Test
  void equalDistanceTest() {
    // given
    SectionRequest sectionParam = new SectionRequest(강남역_ID, 판교역_ID, 10);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록_실패함(result);
  }

  @DisplayName("이미 등록되어있는 구간은 다시 등록할 수 없다.")
  @Test
  void duplicateSectionTest() {
    // given
    SectionRequest sectionParam = new SectionRequest(강남역_ID, 광교역_ID, 10);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록_실패함(result);
  }

  @DisplayName("상행, 하행 역 모두 이미 등록되어 있으면 등록 할 수 없다.")
  @Test
  void containsBothStationsTest() {
    // given
    SectionRequest preRegisterParam = new SectionRequest(강남역_ID, 판교역_ID, 5);
    Long lineId = 신분당선.getId();
    지하철_구간_등록_요청(lineId, preRegisterParam);
    SectionRequest sectionParam = new SectionRequest(강남역_ID, 광교역_ID, 10);

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록_실패함(result);
  }

  @DisplayName("상행, 하행 역 모두 이미 등록되어 있지않으면 등록할 수 없음")
  @Test
  void containsNotBothStationsTest() {
    // given
    Long 광화문역_ID = 등록된_역_ID(지하철_역_등록되어_있음("광화문역"));
    Long 신도림역_ID = 등록된_역_ID(지하철_역_등록되어_있음("신도림역"));
    SectionRequest sectionParam = new SectionRequest(광화문역_ID, 신도림역_ID, 5);
    Long lineId = 신분당선.getId();

    //when
    ExtractableResponse<Response> result = 지하철_구간_등록_요청(lineId, sectionParam);

    //then
    지하철_구간_등록_실패함(result);
  }

  private ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionRequest sectionParam) {
    return RestAssured.given().log().all()
        .body(sectionParam)
        .pathParam("id", lineId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/lines/{id}/sections")
        .then().log().all()
        .extract();
  }

  private void 지하철_구간_등록됨(ExtractableResponse<Response> result, Long lineId, Long... expectStationIds) {
    assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    LineResponse actualLine = 노선_단건을_조회한다(lineId).as(LineResponse.class);
    List<Long> lineStationIds = 노선_응답에서_역_ID들을_얻는다(actualLine);
    assertThat(lineStationIds).isEqualTo(Stream.of(expectStationIds)
                                              .collect(Collectors.toList()));
  }

  private void 지하철_구간_등록_실패함(ExtractableResponse<Response> result) {
    assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }
}
