package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AbstractAcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.AcceptanceUtils.assertStatusCode;
import static nextstep.subway.AcceptanceUtils.extractNames;
import static nextstep.subway.line.LineAcceptanceTest.requestApiByCreateLine;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 선릉역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        교대역 = StationAcceptanceTest.createStation("교대역").extract().as(StationResponse.class);
        선릉역 = StationAcceptanceTest.createStation("선릉역").extract().as(StationResponse.class);
        강남역 = StationAcceptanceTest.createStation("강남역").extract().as(StationResponse.class);

        신분당선 = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 교대역.getId(), 선릉역.getId(), 10)).extract().as(LineResponse.class);
    }


    /**
     * given 2개의 역과 노선을 생성하고
     * when 노선 중간에 새로운 역을 등록하면
     * then 노선 조회 시 추가된 역을 찾을 수 있다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        ValidatableResponse response = requestApiByAddSection(new SectionRequest(교대역.getId(), 강남역.getId(), 5));

        assertStatusCode(response, HttpStatus.CREATED);
        assertThat(extractNames(response)).contains("교대역", "선릉역", "강남역");
    }

    public static ValidatableResponse requestApiByAddSection(Long lineId, SectionRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", lineId)
            .then().log().all();
    }
}
