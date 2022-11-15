package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import javax.persistence.*;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SectionResponse {
    private List<Section> sectionList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Section {
        private Long id;
        private Long upStationId;
        private Long downStationId;
        private Long lineId;
        private int distance;
    }

    public static SectionResponse.Section of(nextstep.subway.domain.Section section) {
        return SectionResponse.Section.builder()
                .id(section.getId())
                .upStationId(section.getUpStation().getId())
                .downStationId(section.getDownStation().getId())
                .lineId(section.getLine().getId())
                .distance(section.getDistance())
                .build();
    }
}
