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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

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

    public void addSection(Station newUpStation, Station newDownStation, long sectionDistance) {
        validateStationInSection(newUpStation, newDownStation);

        if (upStation.equals(newDownStation)) {
            changeUpStation(newUpStation, sectionDistance);
            return ;
        }

        if (downStation.equals(newUpStation)) {
            changeDownStation(newDownStation, sectionDistance);
            return ;
        }

        Optional<Section> sameUpSection = getSections().stream()
                .filter(section -> section.getUpStation().equals(newUpStation))
                .findFirst();

        if (sameUpSection.isPresent()) {
            validateNewSectionDistance(sectionDistance, sameUpSection.get().getDistance());
            addSectionBetweenExistingSection(sectionDistance, newDownStation, sameUpSection);
        }
    }

    private void validateStationInSection(Station newUpStation, Station newDownStation) {
        if (hasStation(newUpStation) && hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 이미 등록된 역입니다.");
        }

        if (!hasStation(newUpStation) && !hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 등록되지 않은 역입니다.");
        }
    }

    private boolean hasStation(Station station) {
        return getSections().stream()
                .anyMatch(section -> section.getUpStation().equals(station) || section.getDownStation().equals(station));
    }

    private void addSectionBetweenExistingSection(long newSectionDistance, Station newDownStation, Optional<Section> existingUpSection) {
        if (existingUpSection.isPresent()) {
            Section existingSection = existingUpSection.get();
            Section newDownSection = new Section(newDownStation, existingSection.getDownStation()
                    , existingSection.getDistance() - newSectionDistance);
            addSection(newDownSection);
            existingSection.setDownStation(newDownStation);
            existingSection.setDistance(newSectionDistance);
        }
    }

    private void changeUpStation(Station newUpStation, long newSectionDistance) {
        Optional<Section> upStationSection = getSections().stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();

        if (upStationSection.isPresent()) {
            Section existingSection = upStationSection.get();
            existingSection.setDownStation(newUpStation);
            sections.add(new Section(newUpStation, upStation, newSectionDistance));
            upStation = newUpStation;
            distance += newSectionDistance;
        }
    }

    private void changeDownStation(Station newDownStation, long newSectionDistance) {
        Optional<Section> downStationSection = getSections().stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();

        if (downStationSection.isPresent()) {
            Section existingSection = downStationSection.get();
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
        return new ArrayList<>(sections);
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    private void validateNewSectionDistance(long newSectionDistance, long existingSectionDistance) {
        if (newSectionDistance >= existingSectionDistance) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 거리가 역 사이 길이보다 크거나 같습니다.");
        }
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
