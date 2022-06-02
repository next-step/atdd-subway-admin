package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> list;

    public Sections() {
        list = new ArrayList<>();
    }

    public Sections(List<Section> list) {
        this.list = list;
    }

    public void add(Section section) {
        list.add(section);
    }

    public List<Section> getList() {
        return list;
    }

    public Optional<Section> findSectionWithUpStation(Station upStation) {
        return list.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    public Optional<Section> findSectionWithDownStation(Station downStation) {
        return list.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    public boolean containStation(Station station) {
        return list.stream().anyMatch(section -> section.containsStation(station));
    }

    public boolean containBothStation(Section section) {
        return containStation(section.getUpStation()) && containStation(section.getDownStation());
    }

    public boolean containNoneStation(Section section) {
        return !containStation(section.getUpStation()) && !containStation(section.getDownStation());
    }
}
