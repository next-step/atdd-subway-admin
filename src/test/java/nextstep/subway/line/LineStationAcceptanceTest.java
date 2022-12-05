package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;
import org.springframework.http.HttpStatus;
import nextstep.subway.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("지하철 구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineStationAcceptanceTest extends LineStationAcceptanceTestFixture {

    /**
     * When 지하철 노선에 지하철역 등록 요청하면
     * Then 지하철 노선에 지하철역 등록된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void 지하철_구간_등록() {
        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 10);

        //Then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        //Then
        LineStations 조회된_구간정보_목록 = 구간목록조회(_2호선.getId()).as(LineStations.class);
        assertThat(조회된_구간정보_목록.getLineStations()).containsAnyOf(response.as(LineStation.class));
    }

    /**
     * Given 지하철 역을 노선에 등록하고
     * When 지하철 노선에 등록된 구간정보 목록을 조회하면
     * Then 지하철 노선에 등록된 구간정보 목록이 조회된다
     */
    @DisplayName("지하철 노선에 등록된 지하철역 목록조회")
    @Test
    void 노선에_등록된_역_목록_조회() {
        //Given
        LineStation 등록된_구간정보 = 구간등록(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 10).as(LineStation.class);

        //When
        ExtractableResponse<Response> response = 구간목록조회(_2호선.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        LineStations 조회된_구간정보_목록 = response.as(LineStations.class);
        assertThat(조회된_구간정보_목록.contains(등록된_구간정보)).isTrue();
    }

    /**
     * Given 지하철 역을 노선에 등록하고
     * When 지하철 노선에 등록된 지하철역 구간정보를 조회하면
     * Then 지하철 노선에 등록된 지하철역 구간정보가 조회된다
     */
    @DisplayName("지하철 노선에 등록된 지하철역 구간정보 조회")
    @Test
    void 노선에_등록된_역_구간정보_조회() {
        //Given
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 상행 종점으로 등록 요청하면
     * Then 지하철 노선에 상행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 상행 종점 등록")
    @Test
    void 상행_종점_등록() {
        //
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 하행 종점으로 등록 요청하면
     * Then 지하철 노선에 하행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 하행 종점 등록")
    @Test
    void 하행_종점_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 노선에 두 역을 등록하고
     * When 등록된 두 역 사이에 기존 역 사이 길이보다 큰 구간길이의 지하철역을 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("지하철 노선의 두 역 사이에 기존 역 사이 길이보다 큰 새로운 역을 등록")
    @Test
    void 구간_길이_초과_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 노선에 두 역을 등록하고
     * When 등록된 두 역을 다시 노선에 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("이미 노선에 등록된 역을 상행역과 하행역으로 등록")
    @Test
    void 등록된_역_중복_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 노선에 두 역을 등록하고
     * When 노선에 등록되지 않은 두 지하철 역을 노선에 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("노선에 등록되지 않은 역을 상행역과 하행역으로 등록")
    @Test
    void 등록되지_않은_역_등록() {
        //
    }
}
