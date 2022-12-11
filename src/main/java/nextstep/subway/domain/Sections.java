package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final String ERROR_MESSAGE_EXIST_STATION = "기존과 동일한 상행/하행선 등록 불가 합니다.";
    private static final String ERROR_MESSAGE_NOT_CONTAIN_STATION = "기존 등록된 상행/하행선이 하나도 포함되어 있지 않습니다.";
    private static final String ERROR_MESSAGE_ONE_STATION = "노선에 등록된 구간이 1이면 삭제 불가능 합니다.";

    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        isValidExistSection(newSection);
        isValidNotContainSection(newSection);
        sections.stream()
                .filter(section -> section.isConnectable(newSection))
                .findFirst()
                .ifPresent(section -> section.reorganize(newSection));
        sections.add(newSection);
    }

    private void isValidNotContainSection(Section section) {
        if (!sections.isEmpty() && isNotContainSection(section)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_CONTAIN_STATION);
        }
    }

    private boolean isNotContainSection(Section section) {
        return getStations().stream()
                .noneMatch(station -> section.stations().contains(station));
    }


    private void isValidExistSection(Section section) {
        if (isContainsAllStation(section)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_EXIST_STATION);
        }
    }

    private boolean isContainsAllStation(Section section) {
        return getStations()
                .containsAll(section.stations());
    }

    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }

    private Optional<Section> deleteDownSection(Station station) {
        Optional<Section> downSection = this.sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst();
        downSection.ifPresent(section -> this.sections.remove(section));
        return downSection;
    }

    public void removeSectionByStation(Station station) {
        isValidMinSizeSection();
        Optional<Section> deleteUpSection = removeUpSection(station);
        Optional<Section> deleteDownSection = removeDownSection(station);

        if (deleteUpSection.isPresent() && deleteDownSection.isPresent()) {
            mergeSection(deleteUpSection.get(), deleteDownSection.get());
            return;
        }

        deleteUpSection.ifPresent(section -> this.sections.remove(section));
        deleteDownSection.ifPresent(section -> this.sections.remove(section));

    }

    private void isValidMinSizeSection() {
        if(sections.size() <= MIN_SECTION_SIZE){
            throw new IllegalArgumentException(ERROR_MESSAGE_ONE_STATION);
        }
    }

    private void mergeSection(Section upSection, Section downSection) {
        downSection.merge(upSection);
        removeSection(upSection);
    }

    private void removeSection(Section section) {
        sections.remove(section);
    }


    private Optional<Section> removeDownSection(Station station) {
        Optional<Section> downSection = this.sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny();

        return downSection;
    }

    private Optional<Section> removeUpSection(Station station) {
        Optional<Section> upSection = this.sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny();

        return upSection;
    }
}
