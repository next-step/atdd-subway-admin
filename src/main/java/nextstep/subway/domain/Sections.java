package nextstep.subway.domain;

import nextstep.subway.exception.SectionNotFoundException;

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
                .filter(section -> upStation.equals(section.getUpStation()))
                .findFirst();
    }

    public Optional<Section> findSectionWithDownStation(Station downStation) {
        return list.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .findFirst();
    }

    public Section getLineUpSection() {
        return list.stream()
                .filter(section -> section.getUpStation() == null)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Station getLineUpStation() {
        return getLineUpSection().getDownStation();
    }

    public Section getLineDownSection() {
        return list.stream()
                .filter(section -> section.getDownStation() == null)
                .findFirst()
                .orElseThrow(() -> {
                    throw new SectionNotFoundException("노선 내 구간을 찾을 수 없습니다");
                });
    }

    public Station getLineDownStation() {
        return getLineDownSection().getUpStation();
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

    @Override
    public String toString() {
        return "Sections{" +
                "list=" + list +
                '}';
    }
}
