package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    public void add(Section newSection) {
        validate(newSection);

        updateWhenSameUpStation(newSection);

        updateWhenSameDownStation(newSection);

        list.add(newSection);
    }

    public List<Station> getStationsInOrder() {
        List<Station> result = new ArrayList<>();
        Section currentSection = firstSection();
        while (true) {
            result.add(currentSection.getDownStation());
            Optional<Section> nextSection = getNextSectionOf(currentSection);
            if (!nextSection.isPresent()) {
                return result;
            }
            currentSection = nextSection.get();
        }
    }

    private void updateWhenSameDownStation(Section newSection) {
        list.stream()
                .filter(it -> it.hasSameDownStationAs(newSection))
                .findFirst()
                .ifPresent(it -> it.updateDownStationToUpStationOf(newSection));
    }

    private void updateWhenSameUpStation(Section newSection) {
        list.stream()
                .filter(it -> it.hasSameUpStationAs(newSection))
                .findFirst()
                .ifPresent(it -> it.updateUpStationToDownStationOf(newSection));
    }

    private void validateSectionIsNull(Section newSection) {
        if (Objects.isNull(newSection)) {
            throw new IllegalArgumentException("추가할 구간이 null");
        }
    }

    private void checkDuplication(Section newSection) {
        list.stream()
                .filter(section -> section.hasSameStations(newSection))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException("구간 중복");
                });
    }

    private void validateDistance(Section newSection) {
        list.stream()
                .filter(section -> section.hasSameUpStationAs(newSection) ||
                        section.hasSameDownStationAs(newSection))
                .findFirst()
                .ifPresent(foundSection -> {
                    if (foundSection.isFirstSection()) {
                        return;
                    }

                    if (!foundSection.canInsert(newSection)) {
                        throw new IllegalArgumentException("길이 오류");
                    }
                });
    }

    private void validateStations(Section newSection) {
        list.stream()
                .filter(section -> section.equalsAtLeastOneStation(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("추가 불가 - 일치 역 없음"));
    }

    private void validate(Section newSection) {
        validateSectionIsNull(newSection);

        if (list.isEmpty()) {
            return;
        }

        checkDuplication(newSection);
        validateDistance(newSection);
        validateStations(newSection);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<Section> getList() {
        return list;
    }

    public Section firstSection() {
        return list.stream()
                .filter(section -> section.isFirstSection())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("첫 번째 구간이 없음"));
    }

    private Optional<Section> getNextSectionOf(Section section) {
        return list.stream()
                .filter(it -> it.isNextSectionOf(section))
                .findFirst();
    }
}
