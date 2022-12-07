package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.TestUtil;
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

        //When
        ExtractableResponse<Response> response = 구간등록(_2호선.getId(), 방배역.getId(), 교대역.getId(), 5);

        //Then
        TestUtil.응답확인(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 지하철 노선에 등록된 상행 종점을 제거 요청하면
     * Then 지하철 노선의 기존 상행 종점이 제거되고 이어져있던 역이 새로운 상행 종점이 된다
     */
    @DisplayName("지하철 노선에 등록된 상행 종점 제거")
    @Test
    void 상행_종점_제거() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);
        구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 5);

        //When
        ExtractableResponse<Response> response = 구간제거(_2호선.getId(), 서초역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.NO_CONTENT);

        //Then
        assertThat(구간목록(구간정보조회(서초역.getId()))).isEmpty();
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 지하철 노선에 등록된 하행 종점을 제거 요청하면
     * Then 지하철 노선의 기존 하행 종점이 제거되고 이어져있던 역이 새로운 하행 종점이 된다
     */
    @DisplayName("지하철 노선에 등록된 하행 종점 제거")
    @Test
    void 하행_종점_제거() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);
        구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 5);

        //When
        ExtractableResponse<Response> response = 구간제거(_2호선.getId(), 강남역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.NO_CONTENT);

        //Then
        assertThat(구간목록(구간정보조회(강남역.getId()))).isEmpty();
    }

    /**
     * Given 지하철 노선에 3개 등록하고 (2개 구간)
     * When 세 역 중 가운데 역을 제거 요청하면
     * Then 가운데 역이 제거되고
     * Then 나머지 두 역이 하나의 구간으로 재배치되며
     * Then구간 제거 전의 두 구간 길이의 합은 재배치된 구간의 길이와 같다
     */
    @DisplayName("지하철 노선에 등록된 두 역 사이의 역을 제거")
    @Test
    void 중간역_구간에서_제거() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);
        구간등록(_2호선.getId(), 교대역.getId(), 강남역.getId(), 5);

        //When
        ExtractableResponse<Response> response = 구간제거(_2호선.getId(), 교대역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.NO_CONTENT);

        //Then
        assertThat(구간목록(구간정보조회(교대역.getId()))).isEmpty();

        //Then
        List<SectionResponse> 조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId()));
        assertThat(구간포함(조회된_구간정보_목록, 서초역.getId(), 강남역.getId(), 10)).isTrue();
    }

    /**
     * 예외케이스
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 노선에 등록된 두 역 중 한 역을 제거 요청 하면
     * Then 지하철 노선에 등록된 역이 제거되지 않는다
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거")
    @Test
    void 하나뿐인_구간_제거() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);

        //When
        ExtractableResponse<Response> response = 구간제거(_2호선.getId(), 서초역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.BAD_REQUEST);

        //Then
        assertThat(구간목록(구간정보조회(서초역.getId()))).isNotEmpty();
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 노선에 등록된 역이 아닌 역을 제거 요청 하면
     * Then 지하철 노선에 등록된 어떤 역도 제거되지 않는다
     */
    @DisplayName("지하철 노선에 등록되어 있지 않은 역을 제거")
    @Test
    void 등록되지_않은_역_노선에서_제거() {
        //Given
        구간등록(_2호선.getId(), 서초역.getId(), 교대역.getId(), 5);
        Stream<SectionResponse> 제거요청_전_조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId())).stream().sorted();

        //When
        ExtractableResponse<Response> response = 구간제거(_2호선.getId(), 강남역.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.BAD_REQUEST);

        //Then
        Stream<SectionResponse> 제거요청_후_조회된_구간정보_목록 = 구간목록(구간목록조회(_2호선.getId())).stream().sorted();
        assertThat(제거요청_전_조회된_구간정보_목록).isEqualTo(제거요청_후_조회된_구간정보_목록);
    }
}
