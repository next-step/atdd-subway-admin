package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    List<Section> sections = new ArrayList<>();

    public List<Long> getStations() {
        List<Long> stations = new ArrayList<>();
        if (Objects.isNull(sections) || sections.size() == 0) {
            return stations;
        }

        Collections.sort(sections);
        stations.add(sections.get(0).getUpStationId());
        stations.addAll(sections.stream()
                .map(s -> s.getDownStationId())
                .collect(Collectors.toList()));

        return stations;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkValidation(section);

        Section existSection = findSection(section).get(0);

        if (existSection.isExistUpStation(section.getUpStationId())) {
            existSection.updateUpStation(section);
        }

        if (existSection.isExistDownStation(section.getDownStationId())) {
            existSection.updateDownStation(section);
        }

        sections.add(section);
    }

    private void checkValidation(Section section) {
        if (notExist(section)) {
            throw new IllegalArgumentException("구간을 추가하려면 노선 중 1개의 역을 포함하여 입력해야 합니다.");
        }
        if (isExist(section)) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }
    }

    private boolean isExist(Section section) {
        return findSection(section).size() > 1;
    }

    private boolean notExist(Section section) {
        return findSection(section).isEmpty();
    }

    private List<Section> findSection(Section section) {
        List<Section> sectionList = new ArrayList<>();
        sectionList.addAll(sections.stream()
                .filter(s -> s.isExistUpStation(section.getUpStationId()))
                .collect(Collectors.toList()));
        sectionList.addAll(sections.stream()
                .filter(s -> s.isExistDownStation(section.getDownStationId()))
                .collect(Collectors.toList()));
        return sectionList;
    }
}