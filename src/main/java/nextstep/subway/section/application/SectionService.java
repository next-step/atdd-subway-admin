package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStationRepository;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final LineStationRepository lineStationRepository;

    public SectionService(final LineStationRepository lineStationRepository) {
        this.lineStationRepository = lineStationRepository;
    }

    public LineStation addNewLineStation(Line line, LineStations lineStations, Station upStation, Station downStation, Distance distance) {
        return lineStationRepository.save(lineStations.addNewLineStation(line, upStation, downStation, distance));
    }

    public void removeSection(Line line, Station station) {
        LineStations lineStations = new LineStations(lineStationRepository.findLineStations(line, station, station, station));
        lineStationRepository.delete(lineStations.removeSection(station));
    }
}
