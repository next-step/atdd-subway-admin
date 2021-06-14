package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        int location = 0;
        if (sections.size() != 0) {
            location = sectionLocation(newSection);
        }
        this.sections.add(location, newSection);
    }

    private int sectionLocation(Section newSection) {
        int sectionIdx = 0;
        Section originSection = sections.get(sectionIdx);
        if (isUpFinalSection(originSection, newSection)) {
            return sectionIdx;
        }
        boolean createUpSection = false;
        boolean createDownSection = false;
        while (!createUpSection && !createDownSection && sectionIdx < sections.size()) {
            originSection = sections.get(sectionIdx);
            createUpSection = checkUpSectionValidation(originSection, newSection);
            createDownSection = checkDownSectionValidation(originSection, newSection);
            sectionIdx++;
        }

        if (createUpSection) {
            return sectionIdx;
        }

        if (createDownSection) {
            return sectionIdx + 1;
        }

        if (isDownFinalSection(originSection, newSection)) {
            return sections.size();
        }
        throw new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 기존 구간에 존재해야 합니다.");
    }

    private boolean isUpFinalSection(Section originSection, Section newSection) {
        return originSection.isUpFinalSection(newSection);
    }

    private boolean isDownFinalSection(Section originSection, Section newSection) {
        return originSection.isDownFinalSection(newSection);
    }

    private boolean checkUpSectionValidation(Section originSection, Section newSection) {
        originSection.checkSameUpStationAndDownStation(newSection);
        if (originSection.isUpSection(newSection)) {
            return true;
        }
        return false;
    }

    private boolean checkDownSectionValidation(Section originSection, Section newSection) {
        if (originSection.isDownSection(newSection)) {
            return true;
        }
        return false;
    }

    public List<Section> getSection() {
        return this.sections;
    }
}
