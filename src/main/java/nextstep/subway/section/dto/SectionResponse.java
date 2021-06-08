package nextstep.subway.section.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;

@Getter
@NoArgsConstructor
public class SectionResponse {

    //TODO
    public static SectionResponse of(final Section section) {
        return new SectionResponse();
    }
}
