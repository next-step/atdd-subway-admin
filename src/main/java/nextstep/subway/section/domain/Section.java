package nextstep.subway.section.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "section")
@Embeddable
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section(Long upStationId, Long downStationId, int distance) {
        upStationId = upStationId;
        downStationId = downStationId;
        distance = distance;
    }

    public List<Long> allStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }
}