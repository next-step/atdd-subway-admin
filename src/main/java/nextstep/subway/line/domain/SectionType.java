package nextstep.subway.line.domain;

public enum SectionType {
    FIRST, MIDDLE, LAST;

    public static boolean equalsFirst(Section section) {
        return section.getSectionType().equals(FIRST);
    }
}
