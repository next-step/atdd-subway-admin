package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SectionPK implements Serializable {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
}
