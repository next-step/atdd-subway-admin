package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.TestLineFactory.stationOf;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
  @DisplayName("역과 역 사이 구간 추가 상행역이 일치한 경우")
  @Test
  void 지하철_기존역_사이_구간_추가_상행역_일치() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "관악역");
    SectionRequest sectionRequest = new SectionRequest( 1L, 3L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    생성_요청이_성공함(response);
  }

  @DisplayName("역과 역 사이 구간 추가 하행역이 일치한 경우")
  @Test
  void 지하철_기존역_사이_구간_추가_하행역_일치() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "관악역");
    SectionRequest sectionRequest = new SectionRequest( 3L, 2L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    생성_요청이_성공함(response);
  }

  @DisplayName("상행 종점역을 추가하여 구간을 생성한다.")
  @Test
  void 지하철_상행_종점역_구간_추가() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "금천구청역");

    SectionRequest sectionRequest = new SectionRequest( 3L, 1L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    생성_요청이_성공함(response);
  }

  @DisplayName("하행 종점역을 추가하여 구간을 생성한다.")
  @Test
  void 지하철_하행_종점역_구간_추가() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "명학역");

    SectionRequest sectionRequest = new SectionRequest( 2L, 3L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    생성_요청이_성공함(response);
  }

  @DisplayName("동일한 구간을 입력하였을 경우 예외를 처리한다.")
  @Test
  void 지하철_구간_추가_중복_발생() {
    // given
    지하철_노선_생성됨();

    SectionRequest sectionRequest = new SectionRequest( 1L, 2L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    중복된_구간이_등록되어_있음(response);
  }

  @DisplayName("기존에 등록된 구간에 연결된 역들과 연결할 수 있는 역이 없을 경우")
  @Test
  void 지하철_구간_추가_연결된_구간_없음() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "명학역");
    지하철_역_생성(4L, "금정역");

    SectionRequest sectionRequest = new SectionRequest( 3L, 4L, 2);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    연결된_구간이_없음(response);
  }

  @DisplayName("기존에 등록된 구간 사이에 추가될 때 거리가 기존 구간보다 높거나 같을 경우")
  @Test
  void 지하철_구간_추가_잘못된_구간간_거리_입력() {
    // given
    지하철_노선_생성됨();
    지하철_역_생성(3L, "관악");

    SectionRequest sectionRequest = new SectionRequest( 1L, 3L, 20);

    // when
    ExtractableResponse<Response> response = 지하철_구간_생성_요청(sectionRequest);

    // then
    구간_길이가_유효하지_않음(response);
  }

  private void 구간_길이가_유효하지_않음(ExtractableResponse<Response> response) {
    assertThat((String) response.jsonPath().get("message.")).isEqualTo("거리의 최소 값은 1 입니다. 입력: -10");
  }

  private void 연결된_구간이_없음(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  private void 중복된_구간이_등록되어_있음(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  private ExtractableResponse<Response> 지하철_구간_생성_요청(SectionRequest sectionRequest) {
    return RestAssured
      .given().log().all()
      .accept(MediaType.ALL_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(sectionRequest)
      .when()
      .post("lines/1/sections")
      .then().log().all()
      .extract();
  }

  private void 지하철_노선_생성됨() {
    Station 석수역 = 지하철_역_생성(1L, "석수역");
    Station 안양역 = 지하철_역_생성(2L, "안양역");
    LineRequest lineRequest = new LineRequest(
      "1호선",
      "blue",
      석수역.getId(),
      안양역.getId(),
      10);

    ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

    생성_요청이_성공함(response);
  }

  private void 생성_요청이_성공함(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
    return RestAssured
      .given().log().all()
      .accept(MediaType.ALL_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(lineRequest)
      .when()
      .post("/lines")
      .then().log().all()
      .extract();
  }

  private ExtractableResponse<Response> 지하철_역_생성_요청(Station station) {
    return RestAssured.given().log().all()
      .body(station)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when()
      .post("/stations")
      .then().log().all()
      .extract();
  }

  private Station 지하철_역_생성(long id, String name) {
    return 지하철_역_생성_요청(stationOf(id, name)).as(Station.class);
  }
}