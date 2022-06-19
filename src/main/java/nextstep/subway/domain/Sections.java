package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            addSection(section);
            return;
        }

        if (isValidAndUpStationOrDownStation(section)) {
            updateExitSection(section);
        }

        addSection(section);
    }

    private boolean isValidAndUpStationOrDownStation(Section section) {
        boolean isUpStationContains = isContains(section.getUpStation());
        boolean isDownStationContains = isContains(section.getDownStation());

        validContains(isUpStationContains, isDownStationContains);
        return isDownStationContains && isDownStationContains;
    }

    private void validContains(boolean isUpStationContains, boolean isDownStationContains) {
        if (isUpStationContains && isDownStationContains) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어 있으면 추가할 수 없습니다.");
        }

        if (!isUpStationContains && !isDownStationContains) {
            throw new IllegalArgumentException("상행역과 하행역 중 하나는 포함되어야 합니다.");
        }
    }

    private boolean isContains(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.isContains(station));
    }

    private void updateExitSection(Section newSection) {
        Optional<Section> optionalSection = this.sections.stream()
                .filter(section -> section.isContains(newSection.getUpStation()) || section.isContains(newSection.getDownStation()))
                .findFirst();

        Section exitSection = optionalSection.get();
        validDistance(newSection.getDistance(), exitSection.getDistance());

        this.sections = this.sections.stream()
                .filter(section -> section.equals(exitSection))
                .map(section -> {section.updateSection(newSection); return section;})
                .collect(Collectors.toList());
    }

    private void validDistance(Long newDistance, Long exitDistance) {
        if (exitDistance <= newDistance) {
            throw new IllegalArgumentException("신규 구간 입력 시 기존 구간보다 길이가 작아야합니다.");
        }
    }

    private void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        return createStations();
    }

    private List<Station> createStations() {
        List<Station> stations = new ArrayList<>();

        Section section = getFirstSection();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        findStations(stations, section);
        return stations;
    }

    private Section getFirstSection() {
        Station station = findUpStation();

        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("노선의 역을 찾을 수 없습니다."));
    }

    private Station findUpStation() {
        List<Station> upStations = this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        return upStations.stream()
                .filter(station -> isNotContainsDownStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("노선의 역을 찾을 수 없습니다."));
    }

    private boolean isNotContainsDownStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(station));
    }

    private void findStations(List<Station> stations, Section section) {
        Optional<Section> optionalSection = findNextDownSection(section);

        while (optionalSection.isPresent()) {
            stations.add(optionalSection.get().getDownStation());
            optionalSection = findNextDownSection(optionalSection.get());
        }
    }

    private Optional<Section> findNextDownSection(Section preSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(preSection.getDownStation()))
                .findFirst();
    }

    public void delete(Station station) {
        validSectionsSizeCheck();
        Section upSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(Section.empty());

        Section downSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(Section.empty());

        if (isValidAndEndStation(upSection, downSection)) {
            createSection(upSection, downSection);
        }

        removeSection(upSection, downSection);
    }

    private void validSectionsSizeCheck() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("구간이 하나인 노선인 경우 구간 삭제 할 수 없습니다.");
        }
    }

    private boolean isValidAndEndStation(Section upSection, Section downSection) {
        if (upSection.isEmpty() && downSection.isEmpty()) {
            throw new IllegalArgumentException("노선에 등록된 역이 아닙니다.");
        }

        if (upSection.isEmpty() || downSection.isEmpty()) {
            return false;
        }

        return true;
    }

    private void createSection(Section upSection, Section downSection) {
        addSection(Section.of(upSection.getUpStation(), downSection.getDownStation(), upSection.getDistance()+downSection.getDistance(), upSection.getLine()));
    }

    private void removeSection(Section upSection, Section downSection) {
        if (upSection != null) {
            sections.remove(upSection);
        }

        if (downSection != null) {
            sections.remove(downSection);
        }
    }
}
