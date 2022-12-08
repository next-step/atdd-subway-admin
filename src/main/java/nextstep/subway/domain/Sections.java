package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections = new ArrayList<>(Arrays.asList(section));
    }

    public void add(Section section) {
        checkStation(section);
        sections.add(section);
    }

    private void checkStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isCheckStation(newSection))
                .findFirst()
                .ifPresent(section -> section.changeStation(newSection));
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeStation(Station station) {
        Section upSection = findSectionbyUpStation(station);
        Section downSection = findSectionbyDownSataion(station);

        // station 이 양 종점인지, middle 인지 판단 후, 삭제 ㄱㄱ
        //checkDeleteMiddle(upSection, downSection);

        deleteMiddle(upSection, downSection);
    }

    private void deleteMiddle(Section upSection, Section downSection) {
        downSection.refreshWith(upSection);
        sections.remove(upSection);
    }

    private Section findSectionbyUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst().get();
    }

    private Section findSectionbyDownSataion(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst().get();
    }
}
