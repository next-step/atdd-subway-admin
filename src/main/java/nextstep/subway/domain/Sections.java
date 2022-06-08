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

    public List<Section> getList() {
        return list;
    }

    public List<Station> getStationsInOrder() {
        List<Station> result = new ArrayList<>();
        Section currentSection = firstSection();
        while (Objects.nonNull(currentSection.getDownStation())) {
            result.add(currentSection.getDownStation());
            currentSection = getNextSectionOf(currentSection);
        }

        return result;
    }

    public Section firstSection() {
        return list.stream()
                .filter(section -> section.isFirstSection())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("첫 번째 구간이 없음"));
    }

    public Section lastSection() {
        return list.stream()
                .filter(section -> section.isLastSection())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("마지막 구간이 없음"));
    }

    public void add(Section newSection) {
        validateSectionIsNull(newSection);

        if (list.isEmpty()) {
            initializeListBy(newSection);
        }

        Optional<Section> targetSection = findSectionToInsert(newSection);
        targetSection.ifPresent(target -> {
            checkDuplication(target, newSection);
            validateDistance(target, newSection);
            rearrange(target, newSection);
        });

        validateStationsOf(newSection);
        list.add(newSection);
    }

    public void removeByDownStation(Station targetStation) {
        Section targetSection = list.stream()
                .filter(section -> targetStation.equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 삭제 대상"));

        Section nextSection = getNextSectionOf(targetSection);

        nextSection.updateUpStationToUpStationOf(targetSection);

        list.remove(targetSection);
    }

    private Section getNextSectionOf(Section section) {
        return list.stream()
                .filter(it -> it.isNextSectionOf(section))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("다음 구간이 존재하지 않음"));
    }

    private Optional<Section> findSectionToInsert(Section newSection) {
        return list.stream()
                .filter(section -> section.hasSameUpStationAs(newSection) || section.hasSameDownStationAs(newSection))
                .findFirst();
    }

    private void initializeListBy(Section newSection) {
        list.add(newSection.generateFirstSection());
        list.add(newSection.generateLastSection());
    }

    private void rearrange(Section target, Section newSection) {
        if (target.hasSameUpStationAs(newSection)) {
            target.updateUpStationToDownStationOf(newSection);
            return;
        }

        if (target.hasSameDownStationAs(newSection)) {
            target.updateDownStationToUpStationOf(newSection);
            return;
        }
    }

    private void validateSectionIsNull(Section newSection) {
        if (Objects.isNull(newSection)) {
            throw new IllegalArgumentException("추가할 구간이 null");
        }
    }

    private void checkDuplication(Section target, Section newSection) {
        if (target.hasSameUpStationAs(newSection) && target.hasSameDownStationAs(newSection)) {
            throw new IllegalArgumentException("구간 중복");
        }
    }

    private void validateDistance(Section target, Section newSection) {
        if (target.isFirstSection() || target.isLastSection()) {
            return;
        }

        if (!target.canInsert(newSection)) {
            throw new IllegalArgumentException("길이 오류");
        }
    }

    private void validateStationsOf(Section newSection) {
        list.stream()
                .filter(section -> section.equalsAtLeastOneStation(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("추가 불가 - 일치 역 없음"));
    }
}
