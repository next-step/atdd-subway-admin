package nextstep.subway.section.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.application.ExceedDistanceException;

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
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public List<Long> allStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

    public boolean longer(Section orgSection) {
        return distance >= orgSection.getDistance();
    }

    public List<Section> splitWhenSameDownStation(Section newSection) {
        validateDistance(newSection);
        return Arrays.asList(
                new Section(upStationId, downStationId, distance - newSection.getDistance()),
                new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance())
        );
    }

    public List<Section> splitWhenSameUpStation(Section newSection) {
        validateDistance(newSection);
        return Arrays.asList(
                new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()),
                new Section(newSection.getDownStationId(), downStationId, distance - newSection.getDistance())
        );
    }

    private void validateDistance(Section newSection) {
        if (newSection.longer(this)) {
            throw new ExceedDistanceException(distance, newSection.getDistance());
        }
    }

    public boolean sameDownStation(Section newSections) {
        return downStationId.equals(newSections.getDownStationId());
    }

    public boolean sameUpStation(Section newSections) {
        return upStationId.equals(newSections.getUpStationId());
    }
}