package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineAddRequest;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    public static final LineAddRequest 이호선_추가 =
            new LineAddRequest("2호선", "bg-green-600", StationTest.사당역.getId(), StationTest.강남역.getId(), 10L);
    public static final LineAddRequest 사호선_추가 =
            new LineAddRequest("4호선", "bg-blue-600", StationTest.사당역.getId(), StationTest.이수역.getId(), 10L);

    public static final Line 이호선 = 이호선_추가.toEntity(StationTest.강남역,  StationTest.사당역);

    @Test
    void 라인_중간_섹션_추가() {
        // given
        Line line = LineTest.이호선;
        Section section = new Section(StationTest.이수역, StationTest.강남역, 5L);
        // when
        line.addSection(section);
        // then
        assertThat(line.containSection(section)).isTrue();
    }
}
