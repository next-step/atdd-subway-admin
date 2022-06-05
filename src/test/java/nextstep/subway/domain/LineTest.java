package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LineTest {
    private Line line;
    private Station finalUpStation;
    private Station finalDownStation;
    private Long lineDistance;

    @BeforeEach
    void setUp() {
        line = new Line("신분당선", "bg-red-600");
        finalUpStation = new Station("강남역");
        finalDownStation = new Station("정자역");
        lineDistance = 30L;
    }

    @Test
    void 이름과_색상으로_노선이_생성되어야_한다() {
        // when
        final Line line = new Line("이름", "색상");

        // then
        assertThat(line).isNotNull();
        assertThat(line).isInstanceOf(Line.class);
    }

    @Test
    void 상행종점과_하행종점을_설정할_수_있어야_한다() {
        // when
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);

        // then
        assertThat(line.getSections().sections())
                .containsExactly(SectionResponse.of(new Section(line, finalUpStation, finalDownStation, lineDistance)));
        assertThat(line.getLineStations().stations())
                .containsExactly(StationResponse.of(finalUpStation), StationResponse.of(finalDownStation));
    }

    @Test
    void 노선을_수정할_수_있어야_한다() {
        // given
        final String newName = "수정된이름";
        final String newColor = "수정된색상";

        // when
        line.update(newName, newColor);

        // then
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @Test
    void 구간과_연관관계를_맺으면_구간_및_새로운_지하철역과의_연관관계가_추가되어야_한다() {
        // when
        line.relateToSection(finalUpStation, finalDownStation, lineDistance);

        // then
        assertThat(line.getSections().sections())
                .contains(SectionResponse.of(new Section(line, finalUpStation, finalDownStation, lineDistance)));
        assertThat(line.getLineStations().stations())
                .containsExactly(StationResponse.of(finalUpStation), StationResponse.of(finalDownStation));
    }
}
