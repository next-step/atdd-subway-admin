package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();

        Collections.sort(sections);
        stationIds.add(sections.get(0).getUpStationId());
        stationIds.addAll(sections.stream()
                .map(s -> s.getDownStationId())
                .collect(Collectors.toList()));

        return stationIds;
    }

    public void addSection(Line line, Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            section.setLine(line);
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
        section.setLine(line);
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

    private boolean isLastSection() {
        return this.sections.size() == 1;
    }

    private List<Section> findSection(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.addAll(this.sections.stream()
                .filter(s -> s.isExistUpStation(section.getUpStationId()))
                .collect(Collectors.toList()));
        sections.addAll(this.sections.stream()
                .filter(s -> s.isExistDownStation(section.getDownStationId()))
                .collect(Collectors.toList()));
        sections.addAll(this.sections.stream()
                .filter(s -> s.isExistUpStation(section.getDownStationId()))
                .collect(Collectors.toList()));
        sections.addAll(this.sections.stream()
                .filter(s -> s.isExistDownStation(section.getUpStationId()))
                .collect(Collectors.toList()));
        return sections;
    }

    public void removeSection(Long stationId) {
        validateRemoveSection(stationId);

        Optional<Section> findSectionByDownStationId = getSectionByDownStationId(stationId);
        Optional<Section> findSectionByUpStationId = getSectionByUpStationId(stationId);

        findSectionByDownStationId.ifPresent(s -> sections.remove(s));
        findSectionByUpStationId.ifPresent(s -> sections.remove(s));

        if (findSectionByDownStationId.isPresent() && findSectionByUpStationId.isPresent()) {
            Section mergeSection = Section.mergeSection(findSectionByDownStationId.get(), findSectionByUpStationId.get());
            sections.add(mergeSection);
        }
    }

    private void validateRemoveSection(Long stationId) {
        if (isLastSection()) {
            throw new IllegalArgumentException("구간이 하나밖에 존재하지 않아 삭제할 수 없습니다.");
        }

        if (getSectionByDownStationId(stationId).isEmpty() && getSectionByUpStationId(stationId).isEmpty()) {
            throw new IllegalArgumentException("구간에 존재하지 않는 역이므로 삭제할 수 없습니다.");
        }
    }

    private Optional<Section> getSectionByUpStationId(Long stationId) {
        return sections.stream()
                .filter(s -> s.getUpStationId().equals(stationId))
                .findFirst();
    }

    private Optional<Section> getSectionByDownStationId(Long stationId) {
        return sections.stream()
                .filter(s -> s.getDownStationId().equals(stationId))
                .findFirst();
    }
}