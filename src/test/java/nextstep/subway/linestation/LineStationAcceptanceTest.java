package nextstep.subway.linestation;

import static nextstep.subway.linestation.LineStationTestFixtures.기존_구간과_상행_종점으로_등록한_구간이_함께_조회됨;
import static nextstep.subway.linestation.LineStationTestFixtures.기존_구간과_하행_종점으로_등록한_구간이_함께_조회됨;
import static nextstep.subway.linestation.LineStationTestFixtures.기존노선과_동일하게_상행_하행역을_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.기존노선의_상행_하행_역과_모두_일치하지_않게_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.등록이_불가하다;
import static nextstep.subway.linestation.LineStationTestFixtures.새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정;
import static nextstep.subway.linestation.LineStationTestFixtures.새로운_역_상행_종점으로_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.새로운_역_하행_종점으로_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.역_사이_새로운역_등록;
import static nextstep.subway.linestation.LineStationTestFixtures.지하철_노선_등록되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.fixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("구간 추가 관련 기능")
class LineStationAcceptanceTest extends TestFixtures {

    @Autowired
    StationRepository stationRepository;
    String stationId1 = "";
    String stationId2 = "";
    String stationId3 = "";
    String stationId4 = "";

    @BeforeEach
    void beforeEach() {
        setUp();
        Station station1 = stationRepository.save(new Station("경기 광주역"));
        Station station2 = stationRepository.save(new Station("중앙역"));
        Station station3 = stationRepository.save(new Station("모란역"));
        Station station4 = stationRepository.save(new Station("미금역"));
        stationId1 = String.valueOf(station1.getId());
        stationId2 = String.valueOf(station2.getId());
        stationId3 = String.valueOf(station3.getId());
        stationId4 = String.valueOf(station4.getId());
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 역 사이에 새로운 역을 등록하면
     * <p>
     * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정 한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addBetween() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        역_사이_새로운역_등록(stationId1, stationId3, "4", lineId);

        //then
        새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정("distance", lineId, "4", "3");
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 새로운 역을 상행 종점으로 등록한다.
     * <p>
     * Then 기존 구간과 상행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void prependUpStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        새로운_역_상행_종점으로_등록(stationId3, stationId1, "4", lineId);

        //then
        기존_구간과_상행_종점으로_등록한_구간이_함께_조회됨("distance", lineId, "7", "4");
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 새로운 역을 하행 종점으로 등록한다.
     * <p>
     * Then 기존 구간과 하행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void appendDownStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        새로운_역_하행_종점으로_등록(stationId2, stationId3, "4", lineId);

        //then
        기존_구간과_하행_종점으로_등록한_구간이_함께_조회됨("distance", lineId, "7", "4");
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 기존역 구간 길이보다 크거나 같은 역을 기존역 사이에 등록하면
     * <p>
     * Then 등록이 불가하다.
     */
    @DisplayName("기존역 구간 길이보다 크거나 같으면 새로운역 등록 불가하다.")
    @Test
    void validateLength() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록(stationId1, stationId3, "8", lineId);

        //then
        등록이_불가하다(response);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 기존노선과 동일하게 상행 하행역을 등록하면
     * <p>
     * Then 등록이_불가하다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void validateAlreadyExistsStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존노선과_동일하게_상행_하행역을_등록(stationId1, stationId2, "4", lineId);

        //then
        등록이_불가하다(response);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 등록하려는 역이 기존노선의 상행 하행 역 모두 일치하지 않으면
     * <p>
     * Then 등록이_불가하다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않으면 추가할 수 없다.")
    @Test
    void validateNotExistsStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존노선의_상행_하행_역과_모두_일치하지_않게_등록(stationId3, stationId4, "4", lineId);

        //then
        등록이_불가하다(response);
    }
}
