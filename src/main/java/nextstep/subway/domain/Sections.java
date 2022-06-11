package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sectionList = new ArrayList<>();

    protected Sections() {

    }

    public List<Section> getSectionList() {
        return new ArrayList<>(sectionList);
    }

    public void add(Section section) {
        validateStations(section.getUpStation(), section.getDownStation());
        sectionList.add(section);
    }

    public void addSectionBetweenTwoStation(Station newUpStation, Station newDownStation, long sectionDistance) {
        Optional<Section> existingUpSection = getSectionList().stream()
                .filter(section -> section.getUpStation().equals(newUpStation))
                .findFirst();

        if (existingUpSection.isPresent()) {
            validateNewSectionDistance(sectionDistance, existingUpSection.get().getDistance());
            addSectionFromUpStation(existingUpSection.get(), newDownStation, sectionDistance);
            return ;
        }

        Optional<Section> existingDownSection = getSectionList().stream()
                .filter(section -> section.getDownStation().equals(newDownStation))
                .findFirst();

        if (existingDownSection.isPresent()) {
            validateNewSectionDistance(sectionDistance, existingDownSection.get().getDistance());
            addSectionFromDownSection(existingDownSection.get(), newUpStation, sectionDistance);
        }
    }

    public Optional<Section> findSectionByUpStation(Station upStation) {
        return sectionList.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    public Optional<Section> findSectionByDownStation(Station downStation) {
        return sectionList.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    private void addSectionFromUpStation(Section existingSection, Station newDownStation, long newSectionDistance) {
        Section newDownSection = new Section(newDownStation, existingSection.getDownStation()
                , existingSection.getDistance() - newSectionDistance);
        sectionList.add(newDownSection);
        existingSection.setDownStation(newDownStation);
        existingSection.setDistance(newSectionDistance);
    }

    private void addSectionFromDownSection(Section existingSection, Station newUpStation, long newSectionDistance) {
        Section newDownSection = new Section(newUpStation, existingSection.getDownStation(), newSectionDistance);
        sectionList.add(newDownSection);
        existingSection.setDownStation(newUpStation);
        existingSection.setDistance(existingSection.getDistance() - newSectionDistance);
    }

    private void validateNewSectionDistance(long newSectionDistance, long existingSectionDistance) {
        if (newSectionDistance >= existingSectionDistance) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 거리가 역 사이 길이보다 크거나 같습니다.");
        }
    }

    private void validateStations(Station newUpStation, Station newDownStation) {
        if (hasStation(newUpStation) && hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 이미 등록된 역입니다.");
        }

        if (!hasStation(newUpStation) && !hasStation(newDownStation)) {
            throw new IllegalArgumentException("두 역 모두 등록되지 않은 역입니다.");
        }
    }

    private boolean hasStation(Station station) {
        return getSectionList().stream()
                .anyMatch(section -> section.getUpStation().equals(station) || section.getDownStation().equals(station));
    }

}
