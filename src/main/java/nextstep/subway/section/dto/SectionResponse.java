package nextstep.subway.section.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;

@Getter
@NoArgsConstructor
public class SectionResponse {

    //TODO : 다음 미션때 작업
    public static SectionResponse of(final Section section) {
        return new SectionResponse();
    }
}
