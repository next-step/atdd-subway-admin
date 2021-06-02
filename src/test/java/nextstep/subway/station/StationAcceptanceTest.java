package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    public final static StationRequest 강남역 = new StationRequest("강남역");
    public final static StationRequest 역삼역 = new StationRequest("역삼역");
    public final static StationRequest 수진역 = new StationRequest("수진역");
    public final static StationRequest 모란역 = new StationRequest("모란역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        지하철역_생성_헤더_검증(response, 1L);

        StationResponse stationResponse = response.as(StationResponse.class);
        지하철역_생성_본문_검증(stationResponse, 1L, 강남역);
    }

    private void 지하철역_생성_본문_검증(StationResponse stationResponse, Long exceptedId, StationRequest request) {
        assertThat(stationResponse.getId())
                .isEqualTo(exceptedId);
        assertThat(stationResponse.getName())
                .isEqualTo(request.getName());
        assertThat(stationResponse.getCreatedDate())
                .isNotNull();
        assertThat(stationResponse.getModifiedDate())
                .isNotNull();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createStationWithDuplicateName() {
        return Stream.of(
                dynamicTest("지하철역 생성 요청", 지하철역_생성_요청_및_체크(강남역, 1L)),
                dynamicTest("기존에 존재하는 지하철역 이름으르 재생성시 요청", () -> {
                    ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

                    생성_실패_검증(response);
                })
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getStations() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 1L)),
                dynamicTest("역삼역을 생성한다", 지하철역_생성_요청_및_체크(역삼역, 2L)),
                dynamicTest("지하철역을 조회한다", () -> {
                    ExtractableResponse<Response> response = 지하철역_목록_조회_요청();
                    지하철역_목록_조회_헤더_검증(response, 1L, 2L);
                })
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @TestFactory
    Stream<DynamicTest> deleteStation() {
        return Stream.of(
                dynamicTest("강남역을 생성한다", 지하철역_생성_요청_및_체크(강남역, 1L)),
                dynamicTest("지하철역을 제거한다", () -> {
                    ExtractableResponse<Response> response = 지하철역_삭제_요청(1L);
                    지하철역_삭제_헤더_검증(response);
                })
        );
    }


    public static Executable 지하철역_생성_요청_및_체크(StationRequest stationRequest, Long exceptedId) {
        return () -> { ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);
            지하철역_생성_헤더_검증(response, exceptedId);
        };
    }

    private static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return response;
    }

    private static ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
        return response;
    }

    private static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }


    private static void 지하철역_생성_헤더_검증(ExtractableResponse<Response> response, Long id) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.header("Location").split("/")[2])
                .isEqualTo(String.valueOf(id));
    }

    private static void 생성_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static void 지하철역_삭제_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private static void 지하철역_목록_조회_헤더_검증(ExtractableResponse<Response> response, Long ...exceptedIds) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactlyInAnyOrder(exceptedIds);
    }
}
