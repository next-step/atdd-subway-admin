package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.line.exception.SectionExceptionCode;

import java.util.Optional;

public class SectionDeleteManager {

    private final Line line;
    private final Optional<Section> sectionOfUpStation;
    private final Optional<Section> sectionOfDownStation;

    private final SectionDeleteStrategy deleteStrategy;

    public SectionDeleteManager(Line line, Optional<Section> sectionOfUpStation,
            Optional<Section> sectionOfDownStation) {
        this.line = line;
        this.sectionOfUpStation = sectionOfUpStation;
        this.sectionOfDownStation = sectionOfDownStation;
        this.deleteStrategy = matchStrategy();
    }

    public SectionDeleteStrategy matchStrategy() {
        if(isFirstSection()) {
            return new FirstSectionDeleteStrategy();
        }

        if(isMiddleSection()) {
            return new MiddleSectionDeleteStrategy();
        }

        return new LastSectionDeleteStrategy();
    }

    private boolean isFirstSection() {
        return sectionOfUpStation.isPresent() && !sectionOfDownStation.isPresent();
    }

    private boolean isMiddleSection() {
        return sectionOfUpStation.isPresent() && sectionOfDownStation.isPresent();
    }

    public void delete() {
        deleteStrategy.delete(line, sectionOfUpStation, sectionOfDownStation);
    }

}
