package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.utils.TestUtil;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
public class LineStationAcceptanceTest extends LineStationAcceptanceTestFixture {

    /**
     * When 지하철 노선에 지하철역 등록 요청하면
     * Then 지하철 노선에 지하철역 등록된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void 지하철_구간_등록() {
        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10);

        //Then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId()));
        assertThat(조회된_구간정보_목록).containsAnyOf(구간정보(response));
    }

    /**
     * Given 지하철 역을 노선에 등록하고
     * When 지하철 노선에 등록된 구간정보 목록을 조회하면
     * Then 지하철 노선에 등록된 구간정보 목록이 조회된다
     */
    @DisplayName("지하철 노선에 등록된 구간정보 목록조회")
    @Test
    void 노선에_등록된_구간정보_목록_조회() {
        //Given
        SectionResponse 등록된_구간정보 = 구간정보(구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10));

        //When
        ExtractableResponse<Response> response = 구간목록조회(_2호선.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(response);
        assertThat(조회된_구간정보_목록.contains(등록된_구간정보)).isTrue();
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 상행 종점으로 등록 요청하면
     * Then 지하철 노선에 상행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 상행 종점 등록")
    @Test
    void 상행_종점_등록() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10);
        Station 방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(Station.class);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 방배역.getId(), 서초역.getId(), 10);

        //Then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId()));
        assertThat(조회된_구간정보_목록).containsAnyOf(구간정보(response));
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 하행 종점으로 등록 요청하면
     * Then 지하철 노선에 하행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 하행 종점 등록")
    @Test
    void 하행_종점_등록() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10);
        Station 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(Station.class);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 10);

        //Then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId()));
        assertThat(조회된_구간정보_목록).containsAnyOf(구간정보(response));
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
        Station 방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(Station.class);
        SectionResponse 등록된_구간정보 = 구간정보(구간등록(_2호선.getId(), 방배역.getId(), 서초역.getId(), 10));
        SectionResponse 등록된_구간정보2 = 구간정보(구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10));

        //When
        ExtractableResponse<Response> response = 구간정보조회(서초역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(response);
        assertAll(
                () -> assertThat(조회된_구간정보_목록.contains(등록된_구간정보)).isTrue(),
                () -> assertThat(조회된_구간정보_목록.contains(등록된_구간정보2)).isTrue()
        );
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 지하철 노선에 새로운 역을 등록된 두 역 사이에 등록 요청하면
     * Then 지하철 노선에 지하철 역이 등록된다
     */
    @DisplayName("지하철 노선에 등록된 두 역 사이에 지하철 역 등록")
    @Test
    void 두_역_사이에_역_등록() {
        //Given
        Station 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(Station.class);
        Station 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(Station.class);
        Station 선릉역 = StationAcceptanceTest.지하철역_생성("선릉역").as(Station.class);
        구간정보(구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10));
        구간정보(구간등록(_2호선.getId(), 강남역.getId(), 선릉역.getId(), 10));

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 4);
        ExtractableResponse<Response> response2 = 구간등록(_2호선.getId(), 역삼역.getId(), 선릉역.getId(), 6);

        //Then
        assertAll(
                () -> TestUtil.응답확인(response, HttpStatus.CREATED),
                () -> TestUtil.응답확인(response2, HttpStatus.CREATED)
        );

        //Then
        List<SectionResponse> 교대역_중심_구간목록 = 구간목록(구간정보조회(교대역.getId()));
        List<SectionResponse> 역삼역_중심_구간목록 = 구간목록(구간정보조회(역삼역.getId()));
        assertAll(
                () -> assertThat(구간포함(교대역_중심_구간목록, 서초역.getId(), 교대역.getId(), 4)).isTrue(),
                () -> assertThat(구간포함(교대역_중심_구간목록, 교대역.getId(), 강남역.getId(), 6)).isTrue(),
                () -> assertThat(구간포함(역삼역_중심_구간목록, 강남역.getId(), 역삼역.getId(), 4)).isTrue(),
                () -> assertThat(구간포함(역삼역_중심_구간목록, 역삼역.getId(), 선릉역.getId(), 6)).isTrue()
        );

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId()));
        assertAll(
                () -> assertThat(구간포함(조회된_구간정보_목록, 서초역.getId(), 강남역.getId(), 10)).isFalse(),
                () -> assertThat(구간포함(조회된_구간정보_목록, 강남역.getId(), 선릉역.getId(), 10)).isFalse()
        );

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
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10);
        Station 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(Station.class);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 15);
        ExtractableResponse<Response> response2 = 구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 15);

        //Then
        assertAll(
                () -> TestUtil.응답확인(response, HttpStatus.BAD_REQUEST),
                () -> TestUtil.응답확인(response2, HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * 예외케이스
     * Given 노선에 두 역을 등록하고
     * When 등록된 두 역 사이에 기존 역 사이 길이와 같은 구간길이의 지하철역을 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("지하철 노선의 두 역 사이에 기존 역 사이 길이와 같은 새로운 역을 등록")
    @Test
    void 동일_구간_길이_등록() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 10);
        Station 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(Station.class);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> response2 = 구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 10);

        //Then
        assertAll(
                () -> TestUtil.응답확인(response, HttpStatus.BAD_REQUEST),
                () -> TestUtil.응답확인(response2, HttpStatus.BAD_REQUEST)
        );
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
        //Given
        Station 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(Station.class);
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);
        구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 5);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 20);
        ExtractableResponse<Response> response2 = 구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 20);
        ExtractableResponse<Response> response3 = 구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 20);

        //Then
        assertAll(
                () -> TestUtil.응답확인(response, HttpStatus.BAD_REQUEST),
                () -> TestUtil.응답확인(response2, HttpStatus.BAD_REQUEST),
                () -> TestUtil.응답확인(response3, HttpStatus.BAD_REQUEST)
        );
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
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 강남역.getId(), 5);
        Station 방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(Station.class);
        Station 교대역 = StationAcceptanceTest.지하철역_생성("교대역").as(Station.class);

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 방배역.getId(), 교대역.getId(), 5);

        //Then
        TestUtil.응답확인(response, HttpStatus.BAD_REQUEST);
    }
}
