package nextstep.subway.line.domain;

import java.util.Optional;

public class SplitedSectionsFactory {
    private SplitedSectionsFactory() {
        throw new IllegalAccessError("유틸 클래스이기떄문에 생성자가 제공되지 않습니다.");
    }

    public static Optional<Sections> generate(SectionMatchingType type, Section findSection, Section section) {
        if (type == SectionMatchingType.DOWN_AND_UP) {
            Section newUpSection = Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance());
            Section newDownSection = Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance());

            return Optional.of(Sections.valueOf(newUpSection, newDownSection));
        } else if (type == SectionMatchingType.DOWN_AND_DOWN) {
            Section newUpSection = Section.valueOf(findSection.getUpStation(), section.getUpStation(), findSection.minusDistance(section));
            Section newDownSection = Section.valueOf(section.getUpStation(), findSection.getDownStation(), section.getDistance());

            return Optional.of(Sections.valueOf(newUpSection, newDownSection));
        } else if (type == SectionMatchingType.UP_AND_UP) {
            Section newUpSection =  Section.valueOf(findSection.getUpStation(), section.getDownStation(), findSection.minusDistance(section));
            Section newDownSection = Section.valueOf(section.getDownStation(), findSection.getDownStation(), section.getDistance());

            return Optional.of(Sections.valueOf(newUpSection, newDownSection));
        } else if (type == SectionMatchingType.UP_AND_DOWN) {
            Section newUpSection = Section.valueOf(section.getUpStation(), section.getDownStation(), section.getDistance());
            Section newDownSection = Section.valueOf(findSection.getUpStation(), findSection.getDownStation(), findSection.getDistance());

            return Optional.of(Sections.valueOf(newUpSection, newDownSection));
        }

        return Optional.empty();
    }

}
