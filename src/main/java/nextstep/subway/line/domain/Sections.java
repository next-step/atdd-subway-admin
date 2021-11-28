package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public void add(Section section) {
        validateDuplicatedStation(section);
        validateNotFoundStation(section);

        this.sections
                .stream()
                .filter(s -> s.hasEqualUpStation(section))
                .findFirst()
                .ifPresent(s -> s.updateUpSection(section));

        this.sections
                .stream()
                .filter(s -> s.hasEqualDownStation(section))
                .findFirst()
                .ifPresent(s -> s.updateDownSection(section));

        this.sections.add(section);
    }

    public List<Station> orderedStations() {
        Section firstSection = findFirstSection();
        return createStations(firstSection);
    }

    private List<Station> createStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        Optional<Section> optionalSection = findNextSection(section);
        while (optionalSection.isPresent()) {
            Section nextSection = optionalSection.get();
            stations.add(nextSection.getDownStation());
            optionalSection = findNextSection(nextSection);
        }
        return stations;
    }

    private Optional<Section> findNextSection(Section section) {
        return this.sections
                .stream()
                .filter(s -> s.getUpStation() == section.getDownStation())
                .findFirst();
    }

    private Section findFirstSection() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : this.sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station firstStation = upStations.get(0);

        return this.sections
                .stream()
                .filter(s -> s.getUpStation() == firstStation)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("노선의 첫번째 구간이 존재하지 않습니다."));
    }

    private void validateDuplicatedStation(Section section) {
        Optional<Section> optionalSection = this.sections
                .stream()
                .filter(s -> s.getUpStation() == section.getUpStation())
                .findFirst()
                .filter(s -> s.getDownStation() == section.getDownStation());

        if (optionalSection.isPresent()) {
            throw new IllegalArgumentException("이미 등록되어 있는 노선입니다.");
        }
    }

    private void validateNotFoundStation(Section section) {
        if (this.sections.isEmpty()) {
            return;
        }

        this.sections
                .stream()
                .filter(s -> s.getUpStation() == section.getUpStation()
                        || s.getDownStation() == section.getUpStation()
                        || s.getUpStation() == section.getDownStation()
                        || s.getDownStation() == section.getDownStation())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다."));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
