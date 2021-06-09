package nextstep.subway.section.domain;

import java.util.*;

public class OrderedSections {

    List<Section> orderedSections;

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
        return !orderedSections.stream()
                .filter(it -> it.getDownStation().getName()
                        .equals(section.getUpStation().getName()))
                .findAny()
                .isPresent();
    }

    private void makeOrderedSectionsFromTop(Section topSection) {
        List<Section> tempSections = new ArrayList<>();
        tempSections.add(topSection);

        Map<String, Section> stationMap = makeStationMap();

        while (topSection != null) {
            topSection = stationMap.get(topSection.getDownStation().getName());
            addNextSection(tempSections, topSection);
        }

        this.orderedSections = tempSections;
    }

    private Map<String, Section> makeStationMap() {
        Map<String, Section> sectionMap = new HashMap<>();
        for (Section it : orderedSections) {
            sectionMap.put(it.getUpStation().getName(), it);
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
        return sectionInput.getDownStation().equals(sectionFromExist.getUpStation());
    }

    private boolean checkFrontMid(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.getUpStation().equals(sectionFromExist.getUpStation())) {
            validateDistance(sectionInput.getDistance(), sectionFromExist.getDistance());

            sectionFromExist.setDistance(sectionFromExist.getDistance() - sectionInput.getDistance());
            sectionFromExist.setUpStation(sectionInput.getDownStation());
            return true;
        }
        return false;
    }

    private boolean checkRearMid(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.getDownStation().equals(sectionFromExist.getDownStation())) {
            validateDistance(sectionInput.getDistance(), sectionFromExist.getDistance());

            sectionFromExist.setDistance(sectionFromExist.getDistance() - sectionInput.getDistance());
            sectionFromExist.setDownStation(sectionInput.getUpStation());
            return true;
        }
        return false;
    }

    private boolean checkRear(Section sectionInput, Section sectionFromExist) {
        if (sectionInput.getUpStation().equals(sectionFromExist.getDownStation())) {
            return true;
        }
        return false;
    }

    private void validateDistance(int inputDistance, int existDistance) {
        if (inputDistance >= existDistance) {
            throw new IllegalArgumentException();
        }
    }

}
