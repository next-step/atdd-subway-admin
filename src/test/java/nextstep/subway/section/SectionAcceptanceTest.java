package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineDataHelper;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.StationDataHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@DisplayName("구간 관련 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    StationDataHelper stationDataHelper;
    @Autowired
    LineDataHelper lineDataHelper;
    
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void saveSection() {
        // given
        Long 강남Id = stationDataHelper.역추가("강남역");
        Long 광교Id = stationDataHelper.역추가("광교역");
        Section section = new Section(강남Id, 광교Id, 10);
        Line savedLine = lineDataHelper.지하철_노선_추가(
                new Line("신분당선", "bg-red-600", new Sections(section))
        );

        // when
        Map<String, String> params = new HashMap<>();
        params.put("downStationId" ,"12");
        params.put("upStationId" ,"20");
        params.put("distance" ,"10");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + savedLine.getId() +"/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
