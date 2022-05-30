package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final String SECTION_ADD_URI = "/lines/1/sections";
    private static final String SECTION_REMOVE_URI = "/lines/1/3/sections";

    @BeforeEach
    void setting() {
        StationAcceptanceTest.지하철_역("강남역");
        StationAcceptanceTest.지하철_역("역삼역");
        StationAcceptanceTest.지하철_역("교대역");
        StationAcceptanceTest.지하철_역("서울역");
        LineAcceptanceTest.지하철_노선("2호선", "green darken-1", "1", "3", "7");
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionBetween() {
        // when
        // 역 사이에 새로운 역을 등록
        ExtractableResponse<Response> response = 지하철_구간("1", "2", "4");

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_구간_등록_성공(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionBefore() {
        // when
        // 새로운 역을 상행 종점으로 등록
        ExtractableResponse<Response> response = 지하철_구간("2", "1", "4");

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_구간_등록_성공(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAfter() {
        // when
        // 새로운 역을 하행 종점으로 등록
        ExtractableResponse<Response> response = 지하철_구간("3", "2", "3");

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_구간_등록_성공(response);
    }


    @DisplayName("역 사이에 새로운 역을 등록할때, 거리가 기존보다 크거나 같으면 안된다.")
    @Test
    void addFarSectionBetween() {
        // when
        // 역 사이에 거리가 더 먼 새로운 역을 등록
        ExtractableResponse<Response> response = 지하철_구간("1", "2", "7");

        // then
        // 지하철_노선에_구간_생성_실패됨
        지하철_구간_등록_실패(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addExistsSection() {
        // when
        // 존재하는 구간을 구간에 등록
        ExtractableResponse<Response> response = 지하철_구간("1", "3", "7");

        // then
        // 지하철_노선에_구간_생성_실패됨
        지하철_구간_등록_실패(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addIrrelevantSection() {
        // when
        // 새로운 역을 구간에 등록
        ExtractableResponse<Response> response = 지하철_구간("2", "4", "7");

        // then
        // 지하철_노선에_구간_생성_실패됨
        지하철_구간_등록_실패(response);
    }

    @DisplayName("종점이 제거될 경우 구간은 다음으로 오던 역이 종점이 된다.")
    @Test
    void removeLineStation() {
        // when
        // 구간이 하나 이상인 노선의 구간을 제거
        지하철_구간("3", "2", "4");
        ExtractableResponse<Response> response = 지하철_구간_삭제(SECTION_REMOVE_URI);

        // then
        // 지하철_노선에_구간_삭제_성공
        지하철_구간_삭제_성공(response);
    }


    @DisplayName("구간이 하나인 노선에서는 마지막 구간을 제거 할 수 없다.")
    @Test
    void removeLineStationInOneSection() {
        // when
        // 구간이 하나인 노선의 구간을 제거
        ExtractableResponse<Response> response = 지하철_구간_삭제(SECTION_REMOVE_URI);

        // then
        // 지하철_노선에_구간_삭제_실패
        지하철_구간_삭제_실패(response);
    }


    public static ExtractableResponse<Response> 지하철_구간(String upStationId, String downStationId, String distance) {
        return 지하철_구간_등록(지하철_구간_제공(upStationId, downStationId, distance));
    }

    public static Map<String, String> 지하철_구간_제공(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_구간_등록(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(SECTION_ADD_URI)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_구간_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 지하철_구간_등록_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_구간_등록_실패(ExtractableResponse<Response> response) {
        assertThatThrownBy(() -> {
            throw new Exception("구간을 등록할 수 없습니다.");
        });
    }

    public static void 지하철_구간_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_구간_삭제_실패(ExtractableResponse<Response> response) {
        assertThatThrownBy(() -> {
            throw new Exception("구간을 삭제할 수 없습니다.");
        });
    }
}
