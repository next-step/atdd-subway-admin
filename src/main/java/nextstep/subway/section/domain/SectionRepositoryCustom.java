package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.util.List;

public interface SectionRepositoryCustom {

    Section getStart(Line line);

    Section getByStation(Line line, Station station);
}
