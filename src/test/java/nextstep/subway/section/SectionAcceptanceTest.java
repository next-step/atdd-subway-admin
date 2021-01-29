package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceUtil;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceUtil;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 등록")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 선릉역;
    private StationResponse 삼성역;

    private LineResponse 신분당선;

    @BeforeEach
    void init() {
        강남역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("강남역")).as(StationResponse.class);
        역삼역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("역삼역")).as(StationResponse.class);
        선릉역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("선릉역")).as(StationResponse.class);
        삼성역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("삼성역")).as(StationResponse.class);
        LineRequest params = LineRequest.builder()
                .name("2호선")
                .color("bg-red-600")
                .upStationId(강남역.getId())
                .downStationId(역삼역.getId())
                .distance(10)
                .build();
        신분당선 = LineAcceptanceUtil.지하철_노선_생성_요청(params).as(LineResponse.class);
    }

    @Test
    @DisplayName("지하철 노선의 구간들을 조회")
    void findLineSections() {

        // when: 지하철_노선_구간_조회_요청
        String uri = "/lines/" + 신분당선.getId() + "/sections";
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회_요청(uri);

        // then : 지하철_노선_구간_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 상행이 같은 경우")
    void addSectionEqualsUpStation() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(강남역, 선릉역, 3);

        // when : 지하철_구간_등록_요청
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then : 지하철_구간_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then : 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationDistances = 지하철_구간_응답_거리(createResponse);
        assertThat(resultStationDistances).containsExactly(3, 7);

    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 하행이 같은 경우")
    void addSectionEqualsDownStation() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(선릉역, 역삼역, 3);

        // when : 지하철_구간_등록_요청
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then : 지하철_구간_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then : 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationDistances = 지하철_구간_응답_거리(createResponse);
        assertThat(resultStationDistances).containsExactly(7, 3);

    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void addSectionFirstUpStation() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(선릉역, 강남역, 3);

        // when : 지하철_구간_등록_요청
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then : 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then : 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationDistances = 지하철_구간_응답_거리(createResponse);
        assertThat(resultStationDistances).containsExactly(3 , 10);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void addSectionLastDownStation() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(역삼역, 선릉역, 3);

        // when : 지하철_구간_등록_요청
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then : 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then : 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationDistances = 지하철_구간_응답_거리(createResponse);
        assertThat(resultStationDistances).containsExactly(10, 3);
    }

    @ParameterizedTest
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ValueSource(ints = {10, 11})
    void validDistance(int input) {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(강남역, 선릉역, input);

        // when, then
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_구간_등록_요청(params);
        지하철_구간_등록_실패됨(response);

    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void validDuplicate() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(강남역, 역삼역, 3);

        // when, then
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_구간_등록_요청(params);
        지하철_구간_등록_실패됨(response);

    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void validNotFound() {

        // given
        SectionRequest params = 지하철_구간_요청_파라미터_생성(선릉역, 삼성역, 3);

        // when, then
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_구간_등록_요청(params);
        지하철_구간_등록_실패됨(response);

    }

    private void 지하철_구간_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private SectionRequest 지하철_구간_요청_파라미터_생성(StationResponse upStation, StationResponse downStation, int distance) {
        return SectionRequest.builder()
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(distance)
                .build();
    }

    private List<Integer> 지하철_구간_응답_거리(ExtractableResponse<Response> createResponse) {
        return createResponse.jsonPath()
                .getObject(".", SectionResponse.class)
                .getSections()
                .getElements()
                .stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

}
