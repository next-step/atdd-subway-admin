package nextstep.subway.line.domain;

public enum SectionType {
    FIRST, MIDDLE, LAST;

    public static boolean equalsFirst(Section oldSectionType) {
        return oldSectionType.getSectionType().equals(FIRST);
    }

    public static boolean equalsFirst(SectionType sectionType) {
        return sectionType.equals(FIRST);
    }
}
