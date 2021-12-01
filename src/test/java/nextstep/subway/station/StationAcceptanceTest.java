package nextstep.subway.station;

import static com.google.common.primitives.Longs.*;
import static io.restassured.RestAssured.*;
import static nextstep.subway.fixtrue.Param.*;
import static nextstep.subway.fixtrue.TestFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixtrue.Param;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    public static final String 강남역 = "강남역";
    public static final String 역삼역 = "역삼역";
    public static final String 신촌역 = "신촌역";
    public static final String 서울역 = "서울역";
    public static final String 용산역 = "용산역";

    @Test
    void 지하철역을_생성한다() {
        // given
        StationRequest stationRequest = new StationRequest(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(new StationRequest(stationGangnam.getName()));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 지하철역을_조회한다() {
        /// given
        StationResponse createResponse1 = 지하철역_등록되어_있음(강남역);
        StationResponse createResponse2 = 지하철역_등록되어_있음(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                                           .map(StationResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(asList(createResponse1.getId(), createResponse2.getId()));
    }

    @Test
    void 지하철역을_제거한다() {
        // given
        StationResponse createResponse = 지하철역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest params) {
        return post("/stations", params);
    }

    public static StationResponse 지하철역_등록되어_있음(String name) {
        지하철역_생성_요청(new StationRequest(name));
        Param param = createParam()
            .addParam("name", name);
        return toResponseData(get("/stations", param), StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청() {
        return given().log().all()
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .get("/stations")
                      .then().log().all()
                      .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        return delete("/stations/{id}", id);
    }
}
