package nextstep.subway.line.application;

import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(MockitoExtension.class)
@DisplayName("노선 관련 기능")
class LineServiceTest {

//    @Mock
//    private LineService lineService;

    @DisplayName("노선 종점 사이 중간역 추가")
    @Test
    void addSection(){
        //given
        Station 강남역 = new Station("강남역");
        Station 사당역 = new Station("사당역");
        Section 종점사이 = Section.of(강남역, 사당역, 4000);
        Line 이호선 = new Line("2호선","green");
        이호선.addSection(종점사이);

        Station 교대역 = new Station("교대역");
        Section 중간역_추가_구간 = Section.of(강남역, 교대역, 1000);

        //when
        이호선.addSection(중간역_추가_구간);

        //then
        assertThat(이호선.getStations().values()).containsExactly(강남역,사당역,교대역);


    }


}