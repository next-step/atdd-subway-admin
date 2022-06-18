package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.BaseLineAcceptanceTest.createLineRequest;
import static nextstep.subway.line.BaseLineAcceptanceTest.findLineRequest;
import static nextstep.subway.station.BaseStationAcceptanceTest.createStationRequest;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseSectionAcceptanceTest {

    private StationResponse 판교역;
    private StationResponse 강남역;
    private LineResponse 신분당선;

    @BeforeEach
    void init() {
        super.setUp();

        판교역 = createStationRequest("판교역").as(StationResponse.class);
        강남역 = createStationRequest("강남역").as(StationResponse.class);
        신분당선 = createLineRequest(LineRequest.of("신분당선", "bg-red-600", 판교역.getId(), 강남역.getId(), 10000))
                .as(LineResponse.class);
    }

    /**
     * Given 지하철 노선을 처음 생성되면
     * Then 구간 정보가 함께 생성된다
     */
    @Test
    @DisplayName("지하철 노선이 처음 생성되면서 구간 정보가 셋팅된다.")
    void addFirstSection() {
        // then
        assertThat(신분당선.getSections()).satisfies(모든구간 -> {
            assertThat(모든구간)
                    .hasSize(1);

            List<StationResponse> stations = 모든구간.get(0).getUpDownStations();
            StationResponse 첫번째_구간_상행역 = stations.get(0);
            StationResponse 첫번째_구간_하행역 = stations.get(1);

            assertThat(첫번째_구간_상행역.getName())
                    .isEqualTo(판교역.getName());
            assertThat(첫번째_구간_하행역.getName())
                    .isEqualTo(강남역.getName());
        });
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 하행 종점역이 포함된 구간정보를 등록하면
     * Then 생성한 지하철 노선와 구간 정보를 응답받을 수 있다
     */
    @Test
    @DisplayName("새로운 하행 종점역이 포함된 구간정보를 등록할 수 있다.")
    void addDownFinalSection() {
        // 신분당선 : [판교역 - 강남역] => [판교역 - 강남역] [강남역 - 신사역]
        // when
        StationResponse 신사역 = createStationRequest("신사역").as(StationResponse.class);
        SectionRequest 강남역_신사역 = SectionRequest.of(강남역.getId(), 신사역.getId(), 5000);

        LineResponse 새로운_신분당선 = createSectionRequest(신분당선.getId(), 강남역_신사역).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections()).satisfies(모든구간 -> {
            assertThat(모든구간)
                    .hasSize(2);
        });

        assertThat(새로운_신분당선.getSections().get(0)).satisfies(첫번째_구간 -> {
            StationResponse 첫번째_구간_상행역 = 첫번째_구간.getUpDownStations().get(0);
            StationResponse 첫번째_구간_하행역 = 첫번째_구간.getUpDownStations().get(1);
            assertThat(첫번째_구간_상행역.getName())
                    .isEqualTo(판교역.getName());
            assertThat(첫번째_구간_하행역.getName())
                    .isEqualTo(강남역.getName());
        });

        assertThat(새로운_신분당선.getSections().get(1)).satisfies(두번째_구간 -> {
            StationResponse 두번째_구간_상행역 = 두번째_구간.getUpDownStations().get(0);
            StationResponse 두번째_구간_하행역 = 두번째_구간.getUpDownStations().get(1);
            assertThat(두번째_구간_상행역.getName())
                    .isEqualTo(강남역.getName());
            assertThat(두번째_구간_하행역.getName())
                    .isEqualTo(신사역.getName());
        });
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 상행 종점역이 포함된 구간정보를 등록하면
     * Then 생성한 지하철 노선와 구간 정보를 응답받을 수 있다
     */
    @Test
    @DisplayName("새로운 상행 종점역이 포함된 구간정보를 등록할 수 있다.")
    void addUpFinalSection() {
        // 신분당선 : [판교역 - 강남역] => [정자역 - 판교역] [판교역 - 강남역]
        // when
        StationResponse 정자역 = createStationRequest("정자역").as(StationResponse.class);
        SectionRequest 정자역_판교역 = SectionRequest.of(정자역.getId(), 판교역.getId(), 3000);

        LineResponse 새로운_신분당선 = createSectionRequest(신분당선.getId(), 정자역_판교역).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections())
                .hasSize(2);

        assertThat(새로운_신분당선.getSections().get(0)).satisfies(첫번째_구간 -> {
            StationResponse 첫번째_구간_상행역 = 첫번째_구간.getUpDownStations().get(0);
            StationResponse 첫번째_구간_하행역 = 첫번째_구간.getUpDownStations().get(1);
            assertThat(첫번째_구간_상행역.getName())
                    .isEqualTo(정자역.getName());
            assertThat(첫번째_구간_하행역.getName())
                    .isEqualTo(판교역.getName());
        });

        assertThat(새로운_신분당선.getSections().get(1)).satisfies(두번째_구간 -> {
            StationResponse 두번째_구간_상행역 = 두번째_구간.getUpDownStations().get(0);
            StationResponse 두번째_구간_하행역 = 두번째_구간.getUpDownStations().get(1);
            assertThat(두번째_구간_상행역.getName())
                    .isEqualTo(판교역.getName());
            assertThat(두번째_구간_하행역.getName())
                    .isEqualTo(강남역.getName());
        });
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 중간에 포함될 구간정보를 등록하면
     * Then 생성한 지하철 노선와 구간 정보를 응답받을 수 있다
     */
    @Test
    @DisplayName("노선 중간에 구간정보를 등록할 수 있다.")
    void addMiddleSection() {
        // 신분당선 : [판교역 - 강남역] => [판교역 - 양재역] [양재역 - 강남역]
        // when
        StationResponse 양재역 = createStationRequest("양재역").as(StationResponse.class);
        SectionRequest 판교역_양재역 = SectionRequest.of(판교역.getId(), 양재역.getId(), 4000);

        LineResponse 새로운_신분당선 = createSectionRequest(신분당선.getId(), 판교역_양재역).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections())
                .hasSize(2);

        assertThat(새로운_신분당선.getSections().get(0)).satisfies(첫번째_구간 -> {
            StationResponse 첫번째_구간_상행역 = 첫번째_구간.getUpDownStations().get(0);
            StationResponse 첫번째_구간_하행역 = 첫번째_구간.getUpDownStations().get(1);
            assertThat(첫번째_구간_상행역.getName())
                    .isEqualTo(판교역.getName());
            assertThat(첫번째_구간_하행역.getName())
                    .isEqualTo(양재역.getName());
        });

        assertThat(새로운_신분당선.getSections().get(1)).satisfies(두번째_구간 -> {
            StationResponse 두번째_구간_상행역 = 두번째_구간.getUpDownStations().get(0);
            StationResponse 두번째_구간_하행역 = 두번째_구간.getUpDownStations().get(1);
            assertThat(두번째_구간_상행역.getName())
                    .isEqualTo(양재역.getName());
            assertThat(두번째_구간_하행역.getName())
                    .isEqualTo(강남역.getName());
        });
    }

    /**
     * Given 새로운 구간들을 등록하고
     * When 지하철 노선 조회 시
     * Then 구간을 순서에 맞게 응답받을 수 있다
     */
    @Test
    @DisplayName("구간 등록 후 조회 시 순서에 맞게 나열된 구간들을 확인할 수 있다.")
    void getSectionsInOrder() {
        // 신분당선 : [판교역 - 강남역] => [정자역 - 판교역] [판교역 - 양재역] [양재역 - 강남역]
        // given
        StationResponse 정자역 = createStationRequest("정자역").as(StationResponse.class);
        SectionRequest 정자역_판교역 = SectionRequest.of(정자역.getId(), 판교역.getId(), 3000);
        createSectionRequest(신분당선.getId(), 정자역_판교역).as(LineResponse.class);

        StationResponse 양재역 = createStationRequest("양재역").as(StationResponse.class);
        SectionRequest 판교역_양재역 = SectionRequest.of(판교역.getId(), 양재역.getId(), 4000);

        LineResponse 새로운_신분당선 = createSectionRequest(신분당선.getId(), 판교역_양재역).as(LineResponse.class);

        // when
        LineResponse 신분당선 = findLineRequest(새로운_신분당선.getId().intValue()).as(LineResponse.class);

        // then
        assertThat(신분당선.getSections()).satisfies(구간 -> {
           SectionResponse 첫번째_구간 = 구간.get(0);
           SectionResponse 두번째_구간 = 구간.get(1);
           SectionResponse 세번째_구간 = 구간.get(2);
           assertThat(첫번째_구간.getUpDownStations().get(0).getName())
                   .isEqualTo("정자역");
            assertThat(첫번째_구간.getUpDownStations().get(1).getName())
                    .isEqualTo("판교역");
            assertThat(두번째_구간.getUpDownStations().get(0).getName())
                    .isEqualTo("판교역");
            assertThat(두번째_구간.getUpDownStations().get(1).getName())
                    .isEqualTo("양재역");
            assertThat(세번째_구간.getUpDownStations().get(0).getName())
                    .isEqualTo("양재역");
            assertThat(세번째_구간.getUpDownStations().get(1).getName())
                    .isEqualTo("강남역");
        });
    }

    /**
     * When 추가할 구간이 기존 역 사이 길이보다 크거나 같으면
     * Then 응답코드 400(BAD REQUEST)를 리턴한다
     */
    @Test
    @DisplayName("추가할 구간의 길이가 기존 역 사이 길이보다 크거나 같으면 BAD REQUEST를 리턴한다.")
    void exceptionWhenTooLongDistance() {
        // when
        StationResponse 양재역 = createStationRequest("양재역").as(StationResponse.class);
        SectionRequest 판교역_양재역 = SectionRequest.of(판교역.getId(), 양재역.getId(), 11000);

        ExtractableResponse<Response> response = createSectionRequest(신분당선.getId(), 판교역_양재역);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 이미 등록된 상하행역을 등록하려고 하면
     * Then 응답코드 400(BAD REQUEST)를 리턴한다
     */
    @Test
    @DisplayName("상하행역이 모두 등록 되어있는 경우 BAD REQUEST를 리턴한다.")
    void exceptionWhenAlreadyRegistered() {
        // when
        SectionRequest 판교역_강남역 = SectionRequest.of(판교역.getId(), 강남역.getId(), 5000);

        ExtractableResponse<Response> response = createSectionRequest(신분당선.getId(), 판교역_강남역);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상하행역이 모두 노선에 존재하지 않으면
     * Then 응답코드 400(BAD REQUEST)를 리턴한다
     */
    @Test
    @DisplayName("상하행역이 모두 등록 되어있지 않은 경우 BAD REQUEST를 리턴한다.")
    void exceptionWhenNotRegistered() {
        // when
        StationResponse 양재시민의숲역 = createStationRequest("양재시민의숲역").as(StationResponse.class);
        StationResponse 양재역 = createStationRequest("양재역").as(StationResponse.class);

        SectionRequest 양재시민의숲역_양재역 = SectionRequest.of(양재시민의숲역.getId(), 양재역.getId(), 5000);

        ExtractableResponse<Response> response = createSectionRequest(신분당선.getId(), 양재시민의숲역_양재역);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 마지막 구간을 제거하려고 하면
     * Then 응답코드 400(BAD REQUEST)를 리턴한다
     */
    @Test
    @DisplayName("존재하는 마지막 구간을 제거하려고 하면 BAD REQUEST를 리턴한다.")
    void exceptionWhenRemoveLastSection() {
        // when
        ExtractableResponse<Response> response = deleteSectionRequest(신분당선.getId(), 판교역.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 구간들을 등록하고
     * When 존재하지 않는 구간을 제거하려고 하면
     * Then 응답코드 400(BAD REQUEST)를 리턴한다
     */
    @Test
    @DisplayName("존재하지 않는 구간을 제거하려고 하면 BAD REQUEST를 리턴한다.")
    void exceptionWhenRemoveNonExistingSection() {
        // given
        StationResponse 신사역 = createStationRequest("신사역").as(StationResponse.class);
        StationResponse 양재역 = createStationRequest("양재역").as(StationResponse.class);
        SectionRequest 강남역_신사역 = SectionRequest.of(강남역.getId(), 신사역.getId(), 5000);

        LineResponse 새로운_신분당선 = createSectionRequest(신분당선.getId(), 강남역_신사역).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = deleteSectionRequest(새로운_신분당선.getId(), 양재역.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 구간들을 등록하고
     * When 역을 입력받아 구간을 제거하면
     * Then 해당 구간이 제거된다
     */
    @Test
    @DisplayName("중간역 제거 후 조회하면 제거 후 업데이트 된 구간 리스트를 확인할 수 있다.")
    void removeMiddleStation() {
        // given
        StationResponse 신사역 = createStationRequest("신사역").as(StationResponse.class);
        SectionRequest 강남역_신사역 = SectionRequest.of(강남역.getId(), 신사역.getId(), 5000);
        createSectionRequest(신분당선.getId(), 강남역_신사역).as(LineResponse.class);

        // when
        deleteSectionRequest(신분당선.getId(), 강남역.getId());
        LineResponse 새로운_신분당선 = findLineRequest(신분당선.getId().intValue()).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections()).satisfies(모든구간 -> {
            assertThat(모든구간)
                    .hasSize(1);
            SectionResponse 삭제_후_구간 = 모든구간.get(0);
            StationResponse 상행역 = 삭제_후_구간.getUpDownStations().get(0);
            StationResponse 하행역 = 삭제_후_구간.getUpDownStations().get(1);

            assertThat(상행역.getName())
                    .isEqualTo("판교역");
            assertThat(하행역.getName())
                    .isEqualTo("신사역");
        });
    }

    /**
     * Given 새로운 구간들을 등록하고
     * When 상행종점역을 제거하면
     * Then 해당 구간이 제거된다
     */
    @Test
    @DisplayName("상행종점역 제거 후 조회하면 제거 후 업데이트 된 구간 리스트를 확인할 수 있다.")
    void removeUpFinalStation() {
        // given
        StationResponse 신사역 = createStationRequest("신사역").as(StationResponse.class);
        SectionRequest 강남역_신사역 = SectionRequest.of(강남역.getId(), 신사역.getId(), 5000);
        createSectionRequest(신분당선.getId(), 강남역_신사역).as(LineResponse.class);

        // when
        deleteSectionRequest(신분당선.getId(), 판교역.getId());
        LineResponse 새로운_신분당선 = findLineRequest(신분당선.getId().intValue()).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections()).satisfies(모든구간 -> {
            assertThat(모든구간)
                    .hasSize(1);
            SectionResponse 삭제_후_구간 = 모든구간.get(0);
            StationResponse 상행역 = 삭제_후_구간.getUpDownStations().get(0);
            StationResponse 하행역 = 삭제_후_구간.getUpDownStations().get(1);

            assertThat(상행역.getName())
                    .isEqualTo("강남역");
            assertThat(하행역.getName())
                    .isEqualTo("신사역");
        });
    }

    /**
     * Given 새로운 구간들을 등록하고
     * When 하행종점역을 제거하면
     * Then 해당 구간이 제거된다
     */
    @Test
    @DisplayName("하행종점역 제거 후 조회하면 제거 후 업데이트 된 구간 리스트를 확인할 수 있다.")
    void removeDownFinalStation() {
        // given
        StationResponse 신사역 = createStationRequest("신사역").as(StationResponse.class);
        SectionRequest 강남역_신사역 = SectionRequest.of(강남역.getId(), 신사역.getId(), 5000);
        createSectionRequest(신분당선.getId(), 강남역_신사역).as(LineResponse.class);

        // when
        deleteSectionRequest(신분당선.getId(), 신사역.getId());
        LineResponse 새로운_신분당선 = findLineRequest(신분당선.getId().intValue()).as(LineResponse.class);

        // then
        assertThat(새로운_신분당선.getSections()).satisfies(모든구간 -> {
            assertThat(모든구간)
                    .hasSize(1);
            SectionResponse 삭제_후_구간 = 모든구간.get(0);
            StationResponse 상행역 = 삭제_후_구간.getUpDownStations().get(0);
            StationResponse 하행역 = 삭제_후_구간.getUpDownStations().get(1);

            assertThat(상행역.getName())
                    .isEqualTo("판교역");
            assertThat(하행역.getName())
                    .isEqualTo("강남역");
        });
    }
}
