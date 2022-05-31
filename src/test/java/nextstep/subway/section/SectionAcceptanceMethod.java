package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static nextstep.subway.BaseAcceptanceTest.*;
import static nextstep.subway.line.LineAcceptanceMethod.*;
import static org.assertj.core.api.Assertions.*;

public class SectionAcceptanceMethod {
    private static final String SECTIONS_URI = "/sections";

    public static ExtractableResponse<Response> 지하철_구간_추가(ExtractableResponse<Response> 지하철노선_생성_응답, SectionRequest 지하철구간_요청) {
        String 지하철구간_요청_URI = String.format("%s%s", 지하철노선_생성_응답.header(HttpHeaders.LOCATION), SECTIONS_URI);
        return post(지하철구간_요청_URI, 지하철구간_요청);
    }

    public static void 지하철구간_추가됨(ExtractableResponse<Response> 구간_추가_응답, SectionRequest 구간_추가_요청) {
        SectionResponse sectionResponse = 구간_추가_응답.jsonPath().getObject(".", SectionResponse.class);
        assertThat(sectionResponse.getUpStationId()).isEqualTo(구간_추가_요청.getUpStationId());
        assertThat(sectionResponse.getDownStationId()).isEqualTo(구간_추가_요청.getDownStationId());
        assertThat(sectionResponse.getDistance()).isEqualTo(구간_추가_요청.getDistance());
    }

    public static void 추가된_지하철구간_조회(ExtractableResponse<Response> 지하철노선_생성_응답, SectionRequest 지하철구간_요청) {
        ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_ID_조회(지하철노선_생성_응답);
        List<Long> stationNames = 지하철노선_조회_응답.jsonPath().getList("stations.id", Long.class);
        assertThat(stationNames).contains(지하철구간_요청.getUpStationId(), 지하철구간_요청.getDownStationId());
    }

    public static void 새로운역_추가_안됨(ExtractableResponse<Response> 구간_추가_응답) {
    }
}
