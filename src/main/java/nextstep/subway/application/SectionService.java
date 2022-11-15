package nextstep.subway.application;

import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    public Section generate(Station upStation, Station downStation, long distance) {
        return new Section(upStation, downStation, distance);
    }
}
