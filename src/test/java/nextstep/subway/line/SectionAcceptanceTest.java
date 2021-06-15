package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역;
    StationResponse 광교역;

    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = 지하철_역_등록되어_있음("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_등록되어_있음(
                    new LineRequest("신분당선", "red darken-1",
                        강남역.getId(), 광교역.getId(), 120)).as(LineResponse.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    void name() { // TODO Happy Path 메서드명 바꾸고 시작
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    void name2() { // TODO Happy Path 메서드명 바꾸고 시작
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    void name3() { // TODO Happy Path 메서드명 바꾸고 시작

    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void name4() { // TODO 메서드명 바꾸고 시작
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void name5() { // TODO 메서드명 바꾸고 시작
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    void name6() { // TODO 메서드명 바꾸고 시작
    }
}
