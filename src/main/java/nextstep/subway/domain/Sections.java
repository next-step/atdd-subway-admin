package nextstep.subway.domain;

import nextstep.subway.error.exception.SectionInvalidException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    public Sections(Line line, Section section) {
        this.sections = new ArrayList<>();
        sections.add(new Section(null, section.getUpStation(), 0, line));
        sections.add(new Section(section.getDownStation(), null, 0, line));
        sections.add(section);
    }

    public void add(Section newSection) {
        checkAlreadyExist(newSection);
        if (addIfFirstSection(newSection)) {
            return ;
        }
        if (addIfEndSection(newSection)) {
            return ;
        }
        addIfIntersection(newSection);
    }

    private boolean addIfFirstSection(Section newSection) {
        Section firstSection = getFirstSection()
                .orElseThrow(() -> new SectionInvalidException("first section이 없는 라인입니다."));
        if (firstSection.canAddFirstSection(newSection)) {
            firstSection.arrangeFirstSection(newSection);
            sections.add(newSection);
            return true;
        }
        return false;
    }

    private boolean addIfEndSection(Section newSection) {
        List<Section> sectionsInOrder = getSectionsInOrder();
        Section endSection = sectionsInOrder.get(sectionsInOrder.size() - 1);
        if (endSection.canAddEndSection(newSection)) {
            endSection.arrangeEndSection(newSection);
            sections.add(newSection);
            return true;
        }
        return false;
    }

    private void addIfIntersection(Section newSection) {
        Section upperSection = getTargetUpperSection(newSection).orElseThrow(()
                -> new SectionInvalidException("상행역과 하행역 둘 다 하나도 포함되어 있지 않습니다."));
        if (upperSection.isShorterThen(newSection)) {
            throw new SectionInvalidException("distance가 기존 구간의 보다 큰 길이 입니다.");
        }
        sections.add(newSection);
        upperSection.arrangeInterSection(newSection);
    }

    private Optional<Section> getTargetUpperSection(Section newSection) {
        return sections.stream()
                .filter(s -> s.canAddInterSection(newSection))
                .findFirst();
    }

    public List<Station> getStationsInOrder() {
        // section을 downStation을 기준으로 변경
        return getSectionsInOrder().stream()
                .filter(s -> s.getDownStation() != null)
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Section> getSectionsInOrder() {
        // 출발지점 찾기
        Optional<Section> preSection = getFirstSection();

        // section 에 있는 station 순서대로 찾기
        List<Section> result = new ArrayList<>();
        while (preSection.isPresent()) {
            Section section = preSection.get();
            result.add(section);
            preSection = sections.stream()
                    .filter(candidate -> isNextSection(section, candidate))
                    .findFirst();
        }
        return result;
    }

    private Optional<Section> getFirstSection() {
        return sections.stream()
                .filter(s -> s.getUpStation() == null)
                .findFirst();
    }

    private boolean isNextSection(Section section, Section candidateSection) {
        if (section.getDownStation() == null) {
            return false;
        }

        if (candidateSection.getUpStation() == null) {
            return false;
        }

        return candidateSection.getUpStation().getId().equals(section.getDownStation().getId());
    }

    private void checkAlreadyExist(Section section) {
        if (hasSection(section)) {
            throw new SectionInvalidException("이미 존재하는 section 입니다.");
        }
    }

    private boolean hasSection(Section newSection) {
        return sections.stream().anyMatch(s -> s.isSameSection(newSection));
    }
}
