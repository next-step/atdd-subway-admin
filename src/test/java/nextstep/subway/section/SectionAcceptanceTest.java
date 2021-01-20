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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 등록")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 선릉역;

    private LineResponse 신분당선;

    @BeforeEach
    void init() {
        강남역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("강남역")).as(StationResponse.class);
        역삼역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("역삼역")).as(StationResponse.class);
        선릉역 = StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("선릉역")).as(StationResponse.class);
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
    @DisplayName("")
    void findLineSections() {

        // when
        // 지하철_노선_조회_요청
        String uri = "/lines/" + 신분당선.getId() + "/sections";
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회_요청(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 상행이 같은 경우")
    void addSectionEqualsUpStation() {

        // given
        SectionRequest params = SectionRequest.builder()
                .upStationId(강남역.getId())
                .downStationId(선릉역.getId())
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then
        // 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationIds = createResponse.jsonPath()
                .getObject(".", SectionResponse.class)
                .getSections()
                .getElements()
                .stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(3, 7);

    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 하행이 같은 경우")
    void addSectionEqualsDownStation() {

        // given
        SectionRequest params = SectionRequest.builder()
                .upStationId(선릉역.getId())
                .downStationId(역삼역.getId())
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then
        // 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationIds = createResponse.jsonPath()
                .getObject(".", SectionResponse.class)
                .getSections()
                .getElements()
                .stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(7, 3);

    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void addSectionFirstUpStation() {
        // given
        SectionRequest params = SectionRequest.builder()
                .upStationId(선릉역.getId())
                .downStationId(강남역.getId())
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then
        // 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationIds = createResponse.jsonPath()
                .getObject(".", SectionResponse.class)
                .getSections()
                .getElements()
                .stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(3 , 10);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void addSectionLastDownStation() {
        // given
        SectionRequest params = SectionRequest.builder()
                .upStationId(역삼역.getId())
                .downStationId(선릉역.getId())
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // then
        // 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
        List<Integer> resultStationIds = createResponse.jsonPath()
                .getObject(".", SectionResponse.class)
                .getSections()
                .getElements()
                .stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(10, 3);
    }

}
