package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_up_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_line_to_down_station"))
    private Station downStation;

    @Embedded
    private Sections sections = new Sections();

    private Long distance = 0L;

    protected Line() {

    }

    public Line(String name, String color, Station upStation, Station downStation, long distance) {
        validateDistance(distance);
        validateNotSameStation(upStation, downStation);
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        sections.add(new Section(upStation, downStation, distance));
    }

    public void update(String name, String color) {
        validateName(name);
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (upStation.equals(section.getDownStation())) {
            changeUpStation(section.getUpStation(), section.getDistance());
            return ;
        }

        if (downStation.equals(section.getUpStation())) {
            changeDownStation(section.getDownStation(), section.getDistance());
            return ;
        }

        sections.addSectionBetweenTwoStation(section.getUpStation(), section.getDownStation(), section.getDistance());
    }


    private void changeUpStation(Station newUpStation, long newSectionDistance) {
        Optional<Section> currentSection = sections.findSectionByUpStation(newUpStation);

        if (currentSection.isPresent()) {
            Section existingSection = currentSection.get();
            existingSection.setDownStation(newUpStation);
            sections.add(new Section(newUpStation, upStation, newSectionDistance));
            upStation = newUpStation;
            distance += newSectionDistance;
        }
    }

    private void changeDownStation(Station newDownStation, long newSectionDistance) {
        Optional<Section> currentSection = sections.findSectionByDownStation(newDownStation);

        if (currentSection.isPresent()) {
            Section existingSection = currentSection.get();
            existingSection.setDownStation(newDownStation);
            sections.add(new Section(newDownStation, downStation, newSectionDistance));
            downStation = newDownStation;
            distance += newSectionDistance;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections.getSectionList());
    }

    private void validateDistance(long distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 큰 숫자만 입력이 가능합니다.");
        }
    }

    private void validateNotSameStation(Station upStation, Station downStation) {
        if (upStation.getId().longValue() == downStation.getId().longValue()) {
            throw new IllegalArgumentException("상행역과 하행역은 동일한 역으로 지정될 수 없습니다.");
        }
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("이름이 입력되지 않았습니다.");
        }
    }
}
