package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철노선 추가 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역;
    StationResponse 광교역;
    LineResponse 신분당선;
    int 신분당선_distance = 50;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        강남역 = StationTestHelper.지하철역_생성("강남역").as(StationResponse.class);
        광교역 = StationTestHelper.지하철역_생성("광교역").as(StationResponse.class);
        신분당선 = LineTestHelper.지하철_노선_생성(new LineRequest("신분당선", "red",
                        강남역.getId(), 광교역.getId(), 신분당선_distance)).as(LineResponse.class);
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 역과 역 사이에 새로운 역을 추가한다.
     * Then 지하철 노선에 새로운 지하철역이 등록된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void 새로운_중간역_추가() {
        // given
        StationResponse 정자역 = StationTestHelper.지하철역_생성("정자역").as(StationResponse.class);

        // when
        SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(강남역.getId(), 정자역.getId(), 10));

        // then
        LineResponse response = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = response.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(stationNameList).hasSize(3),
                () -> assertThat(stationNameList).containsExactly("강남역", "정자역", "광교역")
        );
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 새로운 상행역을 추가한다.
     * Then 지하철 노선에 새로운 지하철역이 등록된다.
     */
    @DisplayName("새로운 상행 종점역을 등록할 경우")
    @Test
    void 새로운_상행역_추가() {
        // given
        StationResponse 신사역 = StationTestHelper.지하철역_생성("신사역").as(StationResponse.class);

        // when
        SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(신사역.getId(), 강남역.getId(), 0));

        // then
        LineResponse response = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = response.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(stationNameList).hasSize(3),
                () -> assertThat(stationNameList).containsExactly("신사역", "강남역", "광교역")
        );
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 새로운 하행역을 추가한다.
     * Then 지하철 노선에 새로운 지하철역이 등록된다.
     */
    @DisplayName("새로운 하행 종점역을 등록할 경우")
    @Test
    void 새로운_하행역_추가() {
        // given
        StationResponse 신광교역 = StationTestHelper.지하철역_생성("신광교역").as(StationResponse.class);

        // when
        SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(광교역.getId(), 신광교역.getId(), 0));

        // then
        LineResponse response = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = response.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(stationNameList).hasSize(3),
                () -> assertThat(stationNameList).containsExactly("강남역", "광교역", "신광교역")
        );
    }

    //////
    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 역과 역 사이에 새로운 역을 추가한다. 하지만 distance가 기존의 역 보다 큰 값
     * Then 지하철 노선에 새로운 지하철역이 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우에 distance가 기존보다 큰 경우")
    @Test
    void 새로운_중간역_추가_중_실패() {
        // given
        StationResponse 정자역 = StationTestHelper.지하철역_생성("정자역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> addSectionResponse = SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(강남역.getId(), 정자역.getId(), 신분당선_distance + 1));

        // then
        LineResponse lineResponse = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(addSectionResponse.statusCode()).isNotEqualTo(200),
                () -> assertThat(stationNameList).hasSize(2),
                () -> assertThat(stationNameList).containsExactly("강남역", "광교역")
        );
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 역과 역 사이에 새로운 역을 추가하고, 다시 추가를 시도
     * Then 지하철 노선에 새로운 지하철역이 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우에 이미 등록된 구간일 경우 실패")
    @Test
    void 이미_등록된_중간역_추가_중_실패() {
        // given
        StationResponse 정자역 = StationTestHelper.지하철역_생성("정자역").as(StationResponse.class);
        SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(강남역.getId(), 정자역.getId(), 10));

        // when
        ExtractableResponse<Response> addSectionResponse = SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(강남역.getId(), 정자역.getId(), 20));

        // then
        LineResponse lineResponse = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(addSectionResponse.statusCode()).isNotEqualTo(200),
                () -> assertThat(stationNameList).hasSize(3),
                () -> assertThat(stationNameList).containsExactly("강남역", "정자역", "광교역")
        );
    }

    /**
     * Given 새로운 지하철 역 2개 등록한다.
     * When 지하철 노선에 역과 역 사이에 새로운 역을 추가하되, 상행과 하행은 기존에 포함되지 않은 역
     * Then 지하철 노선에 새로운 지하철역이 등록되지 않는다.
     */
    @DisplayName("새로운 역을 등록할 경우에 상행/하행 둘 다 포함되어 있지 않을 경우 실패")
    @Test
    void 상행역_하행역_둘_중_하나도_포함이_되지_않는_역_추가시_실패() {
        // given
        StationResponse 정자역 = StationTestHelper.지하철역_생성("정자역").as(StationResponse.class);
        StationResponse 미금역 = StationTestHelper.지하철역_생성("미금역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> addSectionResponse = SectionTestHelper.지하철에_노선_지하철역_등록(신분당선.getId(), new SectionRequest(정자역.getId(), 미금역.getId(), 20));

        // then
        LineResponse lineResponse = LineTestHelper.지하철_노선_ID로_조회(신분당선.getId()).as(LineResponse.class);
        List<String> stationNameList = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(addSectionResponse.statusCode()).isNotEqualTo(200),
                () -> assertThat(stationNameList).hasSize(2),
                () -> assertThat(stationNameList).containsExactly("강남역", "광교역")
        );
    }
}
