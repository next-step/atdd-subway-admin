package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.List;

public interface LineStationSortAble {
    List<Station> sort(List<Section> sections);
}
