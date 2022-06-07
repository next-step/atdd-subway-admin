package nextstep.subway.domain;

import java.util.List;

public class MatchedSections {
    private final static long BOTH_MATCHED_SECTIONS_COUNT = 2;
    private final static int FIRST_SECTION_INDEX = 0;
    private final static int SECOND_SECTION_INDEX = 1;
    private List<Section> matchedSections;

    protected MatchedSections(final List<Section> matchedSections) {
        this.matchedSections = matchedSections;
    }

    public boolean isBetweenTwoSections() {
        return matchedSections.size() == BOTH_MATCHED_SECTIONS_COUNT;
    }

    public void removeMatchedSections(final List<Section> sections) {
        if(isBetweenTwoSections()) {
            removeAndMergeSections(sections);
            return;
        }
        removeSection(sections);
    }

    private void removeAndMergeSections(final List<Section> sections) {
        final Long mergedDistance = matchedSections.stream()
                .mapToLong(section ->
                        section.getDistance())
                .sum();
        final Section firstSection = getFirstSection();
        final Section secondSection = getSecondSection();
        firstSection.updateDistance(mergedDistance);
        firstSection.updateDownStation(secondSection.getDownStation());
        sections.remove(secondSection);
    }

    private Section getFirstSection() {
        return matchedSections.get(FIRST_SECTION_INDEX);
    }

    private Section getSecondSection() {
        return matchedSections.get(SECOND_SECTION_INDEX);
    }

    private void removeSection(final List<Section> sections) {
        sections.remove(getFirstSection());
    }
}
