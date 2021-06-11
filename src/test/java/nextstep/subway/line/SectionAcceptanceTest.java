package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.line.SectionAcceptanceStep.*;
import static nextstep.subway.station.StationAcceptanceStep.지하철역_등록되어_있음;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Map<String, String> createParams;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 정자역;
    private StationResponse 광교중앙역;
    private LineResponse 신분당선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교중앙역 = 지하철역_등록되어_있음("광교중앙역").as(StationResponse.class);

        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStation", 강남역.getId() + "");
        createParams.put("downStation", 광교역.getId() + "");
        createParams.put("distance", 10 + "");
        신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }

    @DisplayName("이미 존재하는 노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 10);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("지하철 노선에 해당하는 역 목록은 상행-하행 순으로 조회한다.")
    @Test
    void getSortedStations() {
        // given
        지하철_노선에_지하철역_등록되어_있음(신분당선.getId(), 강남역.getId(), 광교역.getId(), 10);
        지하철_노선에_지하철역_등록되어_있음(신분당선.getId(), 광교역.getId(), 광교중앙역.getId(), 10);
        지하철_노선에_지하철역_등록되어_있음(신분당선.getId(), 정자역.getId(), 강남역.getId(), 10);
        List<StationResponse> expected = Arrays.asList(
                정자역,
                강남역,
                광교역,
                광교중앙역
        );

        // when
        LineResponse lineResponse = 지하철_노선_조회_요청(신분당선.getId()).as(LineResponse.class);

        // then
        지하철_노선에_정렬된_지하철역_목록_포함됨(expected, lineResponse.getStations());
    }
}
