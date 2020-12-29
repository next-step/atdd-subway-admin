package nextstep.subway.line.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Section {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
}
