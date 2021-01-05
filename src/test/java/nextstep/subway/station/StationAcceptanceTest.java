package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
  @DisplayName("지하철역을 생성한다.")
  @Test
  void createStation() {
    // when
    ExtractableResponse<Response> response = 지하철_등록_요청("강남역");

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.header("Location")).isNotBlank();
  }

  @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
  @Test
  void createStationWithDuplicateName() {
    // given
    지하철_등록_요청("강남역");

    // when
    ExtractableResponse<Response> response = 지하철_등록_요청("강남역");

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @DisplayName("지하철역을 조회한다.")
  @Test
  void getStations() {
    /// given
    ExtractableResponse<Response> 강남역 = 지하철_등록_요청("강남역");
    ExtractableResponse<Response> 역삼역 = 지하철_등록_요청("역삼역");

    // when
    ExtractableResponse<Response> response = 지하철_목록_조회_요청();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    List<Long> expectedLineIds = Arrays.asList(강남역, 역삼역).stream()
        .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
        .collect(Collectors.toList());
    List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
        .map(it -> it.getId())
        .collect(Collectors.toList());
    assertThat(resultLineIds).containsAll(expectedLineIds);
  }

  @DisplayName("지하철역을 제거한다.")
  @Test
  void deleteStation() {
    // given
    ExtractableResponse<Response> createResponse = 지하철_등록_요청("강남역");

    // when
    String uri = createResponse.header("Location");
    ExtractableResponse<Response> response = 지하철_제거_요청(uri);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  public static ExtractableResponse<Response> 지하철_등록_요청(String name) {
    StationRequest stationRequest = new StationRequest(name);
    return RestAssured.given().log().all()
        .body(stationRequest)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/stations")
        .then().log().all()
        .extract();
  }

  private ExtractableResponse<Response> 지하철_목록_조회_요청() {
    return RestAssured.given().log().all()
        .when()
        .get("/stations")
        .then().log().all()
        .extract();
  }

  private ExtractableResponse<Response> 지하철_제거_요청(String uri) {
    return RestAssured.given().log().all()
        .when()
        .delete(uri)
        .then().log().all()
        .extract();
  }
}
