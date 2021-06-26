package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    /*
        2호선: [사당 - 역삼]

        1: [교대 - 사당] - 역삼
        2: [사당 - 교대] - 역삼
        3: 사당 - [교대 - 역삼]
        4: 사당 - [역삼 - 교대]
    */
    private static final int 초기화_거리 = 60;
    private static final int 구간_거리 = 30;
    private StationResponse 사당역;
    private StationResponse 교대역;
    private StationResponse 선릉역;
    private LineResponse 지하철_2호선;
    private int 지하철_2호선_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();

        사당역 = 지하철_역_등록되어_있음("사당역");
        교대역 = 지하철_역_등록되어_있음("교대역");
        선릉역 = 지하철_역_등록되어_있음("선릉역");

        지하철_2호선 = 지하철_노선_종점포함_등록되어_있음("2호선", "녹색", 사당역.getId(), 선릉역.getId(), 초기화_거리);
        지하철_2호선_ID = 지하철_2호선.getId().intValue();
    }

    @DisplayName("상행역의 앞에 추가: [교대 - 사당] - 선릉")
    @Test
    void beforeUpStation() {
        // given
        Long[] expectedLineIds = {교대역.getId(), 사당역.getId(), 선릉역.getId()};
        Map<String, String> section = 지하철_구간_생성_정보(교대역.getId(), 사당역.getId(), 구간_거리);

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(지하철_2호선_ID, section);

        // then
        // 지하철_구간_추가_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 지하철_노선_목록_포함됨
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactly(expectedLineIds);
    }

    @DisplayName("상행역의 뒤에 추가: [사당 - 교대] - 선릉")
    @Test
    void afterUpStation() {
        // given
        Long[] expectedLineIds = {사당역.getId(), 교대역.getId(), 선릉역.getId()};
        Map<String, String> section = 지하철_구간_생성_정보(사당역.getId(), 교대역.getId(), 구간_거리);

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(지하철_2호선_ID, section);

        // then
        // 지하철_구간_추가_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 지하철_노선_목록_포함됨
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactly(expectedLineIds);
    }

    @DisplayName("하행역의 앞에 추가: 사당 - [교대 - 선릉]")
    @Test
    void beforedownStation() {
        // given
        Long[] expectedLineIds = {사당역.getId(), 교대역.getId(), 선릉역.getId()};
        Map<String, String> section = 지하철_구간_생성_정보(교대역.getId(), 선릉역.getId(), 구간_거리);

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(지하철_2호선_ID, section);

        // then
        // 지하철_구간_추가_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 지하철_노선_목록_포함됨
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactly(expectedLineIds);

    }

    @DisplayName("하행역의 뒤에 추가: 사당 - [선릉 - 교대]")
    @Test
    void afterDownStation() {
        // given
        Long[] expectedLineIds = {사당역.getId(), 선릉역.getId(), 교대역.getId()};
        Map<String, String> section = 지하철_구간_생성_정보(선릉역.getId(), 교대역.getId(), 구간_거리);

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(지하철_2호선_ID, section);

        // then
        // 지하철_구간_추가_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 지하철_노선_목록_포함됨
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactly(expectedLineIds);
    }

    private ExtractableResponse<Response> 지하철_구간_추가_요청(int lineId, Map<String, String> section) {
        return RestAssured
            .given().log().all()
            .body(section)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(String.format("/lines/%d/sections", lineId))
            .then().log().all()
            .extract();
    }

    private StationResponse 지하철_역_등록되어_있음(String name) {
        Map<String, String> station = new HashMap<String, String>() {{
            put("name", name);
        }};

        return StationAcceptanceTest.지하철_역_등록되어_있음(station).as(StationResponse.class);
    }

    private LineResponse 지하철_노선_종점포함_등록되어_있음(
        String name, String color, Long upStationId, Long downStationId, int distance
    ) {
        Map<String, String> line = new HashMap<String, String>() {{
            put("name", name);
            put("color", color);
            put("upStationId", String.valueOf(upStationId));
            put("downStationId", String.valueOf(downStationId));
            put("distance", String.valueOf(distance));
        }};

        return LineAcceptanceTest.지하철_노선_종점포함_등록되어_있음(line).as(LineResponse.class);
    }

    private Map<String, String> 지하철_구간_생성_정보(Long upStationId, Long downStationId, int distance) {
        return new HashMap<String, String>() {{
            put("upStationId", String.valueOf(upStationId));
            put("downStationId", String.valueOf(downStationId));
            put("distance", String.valueOf(distance));
        }};
    }

}
