package nextstep.subway.section.domain;

import java.util.*;

public class OrderedSections {

    private List<Section> orderedSections;

    private OrderedSections(List<Section> orderedSections) {
        this.orderedSections = orderedSections;
        makeOrderedSectionsFromTop(findTopSection());
    }

    public static OrderedSections of(List<Section> orderedSections) {
        return new OrderedSections(orderedSections);
    }

    public List<Section> get() {
        return Collections.unmodifiableList(orderedSections);
    }

    protected Section findTopSection() {
        return orderedSections.stream()
                .filter(section -> isTop(section))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    private boolean isTop(Section section) {
        return orderedSections.stream()
                .noneMatch(it -> it.isInFrontOf(section));
    }

    private void makeOrderedSectionsFromTop(Section topSection) {
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(topSection);

        Map<String, Section> stationMap = makeStationMap();

        while (topSection != null) {
            topSection = stationMap.get(topSection.downStationName());
            addNextSection(tempSections, topSection);
        }

        this.orderedSections = tempSections;
    }

    private Map<String, Section> makeStationMap() {
        Map<String, Section> sectionMap = new HashMap<>();
        for (Section it : orderedSections) {
            sectionMap.put(it.upStationName(), it);
        }
        return sectionMap;
    }

    private void addNextSection(List<Section> orderedSections, Section section) {
        if (section == null) {
            return;
        }
        if (orderedSections.contains(section)) {
            return;
        }
        orderedSections.add(section);
    }

    public void connectThroughOrderedSections(Section sectionIn) {
        int i = 0;
        boolean isConnected = false;
        while (!isConnected) {
            Section section = orderedSections.get(i);
            isConnected = runningThrough(sectionIn, section);
            ++i;
        }
    }

    private boolean runningThrough(Section sectionIn, Section section) {
        if (checkFront(sectionIn, section)
        || checkFrontMid(sectionIn, section)
        || checkRearMid(sectionIn, section)
        || checkRear(sectionIn, section)) {
            return true;
        }
        return false;
    }

    private boolean checkFront(Section sectionInput, Section sectionFromExist) {
        return sectionInput.isInFrontOf(sectionFromExist);
    }

    private boolean checkFrontMid(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.isInMidFrontOf(sectionFromExist)) {
            sectionFromExist.connectBehindOf(sectionInput);
            return true;
        }
        return false;
    }

    private boolean checkRearMid(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.isInMidRearOf(sectionFromExist)) {
            sectionFromExist.connectInFrontOf(sectionInput);
            return true;
        }
        return false;
    }

    private boolean checkRear(Section sectionInput, Section sectionFromExist) {
        return sectionInput.isBehindOf(sectionFromExist);
    }

}
