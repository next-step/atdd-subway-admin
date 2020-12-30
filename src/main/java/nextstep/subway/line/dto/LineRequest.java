package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionFactory;

@Getter
@NoArgsConstructor
public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    public LineRequest(final String name, final String color,
                       final Long upStationId, final Long downStationId,
                       final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(final SectionFactory sectionFactory) {
        return Line.of(name, color, toSection(sectionFactory));
    }

    public Section toSection(final SectionFactory sectionFactory) {
        return sectionFactory.create(upStationId, downStationId, distance);
    }
}
