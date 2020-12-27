package nextstep.subway.line.domain.sections;

import java.util.ArrayList;
import java.util.List;

public class SectionsInLineExplorer {
    public List<Long> getStationIdsOrderBySection(final Sections sections) {
        List<Long> stationIds = new ArrayList<>();

        Section endUpSection = sections.findEndUpSection();
        stationIds.add(endUpSection.getUpStationId());
        stationIds.add(endUpSection.getDownStationId());

        Section nextSection = sections.findNextSection(endUpSection);
        while (nextSection != null) {
            stationIds.add(nextSection.getDownStationId());
            nextSection = sections.findNextSection(nextSection);
        }

        return stationIds;
    }

    public boolean isInEndSection(final Sections sections, final Section section) {
        Section endUpSection = sections.findEndUpSection();
        Section endDownSection = sections.findEndDownSection();
        return endUpSection.isSameUpWithThatDown(section) || endDownSection.isSameDownWithThatUp(section);
    }
}
