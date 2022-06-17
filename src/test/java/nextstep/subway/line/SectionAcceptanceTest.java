package nextstep.subway.line;

import static nextstep.subway.line.LineCRUD.지하철노선_생성;
import static nextstep.subway.line.LineCRUD.지하철노선_아이디로_조회;
import static nextstep.subway.station.StationCRUD.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequestDto;
import nextstep.subway.line.dto.SectionRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final int 노선_거리 = 10;

    private Long 노선;
    private Long 강남역;
    private Long 선릉역;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성("강남역");
        선릉역 = 지하철역_생성("선릉역");
        LineRequestDto request = new LineRequestDto("2호선", "bg-green-600", 강남역, 선릉역, 노선_거리);

        // Given
        노선 = 지하철노선_생성(request).jsonPath().getLong("id");
    }

    /**
     *   When 노선의 한 구간과 동일한 상행역과 새로운 역을 하행역으로 가지고
     *    And 거리가 노선의 구간보다 짧은 구간을 추가하면
     *   Then 그 구간의 상행역과 하행역 사이에 새로운 역이 추가되고
     *    And 노선의 거리에서 추가된 구간의 거리를 뺀 나머지를 추가된 역과의 거리로 설정된다.
     */
    @Test
    @DisplayName("노선에 구간을 추가하면 조회 시 생성한 노선을 찾을 수 있다.")
    void 지하철구간_추가() {
        Long 역삼역 = 지하철역_생성("역삼역");

        SectionRequestDto sectionRequestDto = new SectionRequestDto(강남역, 역삼역, 4);

        // When
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequestDto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format("/lines/%d/sections", 노선))
                .then().log().all()
                .extract();

        응답코드_검증(response, HttpStatus.OK);

        // Then
        List<String> stationNames = 지하철노선_아이디로_조회(노선).jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsAnyOf("역삼역");
    }
}
