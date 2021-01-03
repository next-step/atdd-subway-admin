package nextstep.subway.section.domain;

public enum AdjustSectionType {

    UP {
        @Override
        public boolean checkSection(Section oldSection, Section newSection) {
            return oldSection.getUpStation().equals(newSection.getUpStation());
        }

        @Override
        public Section createSection(Section oldSection, Section newSection) {
            return new Section(
                    oldSection.getLine(),
                    newSection.getDownStation(),
                    oldSection.getDownStation(),
                    oldSection.getDistance() - newSection.getDistance());
        }
    },

    DOWN {
        @Override
        public boolean checkSection(Section oldSection, Section newSection) {
            return oldSection.getDownStation().equals(newSection.getDownStation());
        }

        @Override
        public Section createSection(Section oldSection, Section newSection) {
            return new Section(
                    oldSection.getLine(),
                    oldSection.getUpStation(),
                    newSection.getUpStation(),
                    oldSection.getDistance() - newSection.getDistance());
        }
    },

    NONE {
        @Override
        public boolean checkSection(Section oldSection, Section newSection) {
            return false;
        }

        @Override
        public Section createSection(Section oldSection, Section newSection) {
            return null;
        }
    };

    public static AdjustSectionType valueOf(Section oldSection, Section newSection) {
        for (AdjustSectionType type : values()) {
            if (type.checkSection(oldSection, newSection)) {
                return type;
            }
        }
        return NONE;
    }

    public abstract boolean checkSection(Section oldSection, Section newSection);
    public abstract Section createSection(Section oldSection, Section newSection);
}
