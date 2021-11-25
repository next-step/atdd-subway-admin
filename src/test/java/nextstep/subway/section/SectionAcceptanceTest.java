package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static nextstep.subway.line.LineAcceptanceTest.역_ID;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성_요청;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationService stationService;

    @DisplayName("지하철 노선 생성시 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> 광교역_생성_응답 = 지하철_역_생성_요청("광교역");

        // when
        지하철_노선_생성_요청("신분당선", "red", 역_ID(강남역_생성_응답), 역_ID(광교역_생성_응답), 10);

        // then
        지하철_구간_생성됨(역_ID(강남역_생성_응답), 역_ID(광교역_생성_응답));
    }

    private void 지하철_구간_생성됨(long upStationId, long downStationId) {
        Optional<Section> optionalSection = 지하철_구간_조회(upStationId, downStationId);
        assertThat(optionalSection.get()).isNotNull();
    }

    private Optional<Section> 지하철_구간_조회(long upStationId, long downStationId) {
        Station upStation = stationService.findStation(upStationId);
        Station downStation = stationService.findStation(downStationId);

        return sectionRepository.findByUpStationAndDownStation(upStation, downStation);
    }
}
