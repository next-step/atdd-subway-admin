package nextstep.subway.section.domain;

import java.util.List;

public class TopSection {
    private Section topSection = null;

    public TopSection(List<Section> sections) {
        this.topSection = findTopSection(sections);
    }

    private Section findTopSection(List<Section> sections) {
        if (sections.isEmpty()) {
            return null;
        }

        Section topSection = sections.get(0);
        Section recentSection = topSection;

        while ((recentSection = findUpperSectionByRecentTopSection(sections, recentSection)) != null) {
            topSection = recentSection;
        }

        return topSection;
    }

    private Section findUpperSectionByRecentTopSection(List<Section> sections, Section recentTopSection) {
        return sections.stream()
                .filter(recentTopSection::isLower)
                .findFirst()
                .orElse(null);
    }

    public Section getTopSection() {
        return topSection;
    }
}
