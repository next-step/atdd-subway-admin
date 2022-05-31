package nextstep.subway.section;

import static java.util.stream.Collectors.toList;
import static nextstep.subway.SubwayAppBehaviors.지하철노선을_생성하고_ID를_반환한다;
import static nextstep.subway.SubwayAppBehaviors.지하철노선을_조회한다;
import static nextstep.subway.SubwayAppBehaviors.지하철역을_생성하고_생성된_ID를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.subway.annotation.SubwayAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철구간 관련 기능")
@SubwayAcceptanceTest
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 새로운 역을 노선에 추가하여 구간을 등록한다.
     * Then 지하철 노선 조회 시 생성된 구간을 찾을 수 있다
     */
    @DisplayName("지하철구간을 등록한다.")
    @Test
    void createSection() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 강남역_ID, 신촌역_ID, 50L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Optional<LineResponse> optionalLineResponse = 지하철노선을_조회한다(lineId);
        assertThat(optionalLineResponse.isPresent()).isTrue();

        LineResponse lineResponse = optionalLineResponse.get();
        List<SectionResponse> sectionResponses = lineResponse.getSectionResponses();
        List<String> stationNames = sectionResponses.stream().map((sectionResponse) -> sectionResponse.getStartStationName()).collect(toList());
        assertThat(stationNames).containsExactly("강남역", "신촌역", "충정로역");
    }

    private ExtractableResponse<Response> 지하철구간을_생성한다(Long lineId, Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured
                .given().contentType(ContentType.JSON).body(params).log().all()
                .when().post(String.format("/lines/%d/sections", lineId))
                .then().log().all()
                .extract();
    }
}
