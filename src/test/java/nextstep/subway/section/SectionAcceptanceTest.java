package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.노선을_생성한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선-라인 관련 인수테스트")
@Sql("/sql/truncate_section.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private LineRepository lineRepository;

    private StationResponse 건대역;
    private StationResponse 뚝섬유원지역;
    private StationResponse 청담역;
    private StationResponse 강남구청역;
    private StationResponse 논현역;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // given
        건대역 = 지하철역을_생성한다("건대역").as(StationResponse.class);
        뚝섬유원지역 = 지하철역을_생성한다("뚝섬유원지역").as(StationResponse.class);
        청담역 = 지하철역을_생성한다("청담역").as(StationResponse.class);
        강남구청역 = 지하철역을_생성한다("강남구청역").as(StationResponse.class);
        논현역 = 지하철역을_생성한다("논현역").as(StationResponse.class);
    }

    /**
     * When 노선에 상행과 하행을 등록하면
     * Then 노선에 역이 등록된다.
     */
    @Test
    public void 노선_7호선_상행_건대_하행_고속터미널_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 건대역.getId(), 뚝섬유원지역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 청담역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    //TODO TEST CASE LIST

    /**
     * When 상행, 하행이 등록된 노선 중간에 역을 등록하면
     * Then 노선 중간에 역이 등록된다. (Happy Path)
     */
    @Test
    public void 역사이에_새로운_구간_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 20).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 상행, 하행이 등록된 노선의 상행 종점에 새로운 역을 등록하면
     * Then 새로운 역이 상행 종점으로 등록된다. (Happy Path)
     */

    /**
     * When 상행, 하행이 등록된 노선의 하행 종점에 새로운 역을 등록하면
     * Then 새로운 역이 하행 종점으로 등록된다. (Happy Path)
     */

    /**
     * When 이미 해당 노선에 존재하는 역을 등록하면
     * Then 구간이 등록되지 않는다.
     */

    /**
     * When 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면
     * Then 구간이 등록되지 않는다.
     */

    /**
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
     * Then 구간이 등록되지 않는다.
     */

    /**
     * When 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 구간이 등록되지 않는다.
     */

    public static ExtractableResponse<Response> 구간을_생성한다(Long id, Long upStationId, Long downStationId, Integer distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }
}
