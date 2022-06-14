package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineStationService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;


    public LineStationService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse addSection(SectionRequest sectionRequest, Long lineId) {

        Line line = lineRepository.findById(lineId).get();
        line.addLineStation(sectionRequest);
        LineStations lineStations = line.getLineStations();

        LineResponse lineResponse = LineResponse.from(line);
        for (LineStation lineStation : lineStations.getLineStations()) {
            Station station = stationRepository.findById(lineStation.getStationId()).get();
            lineResponse.addStationResponses(StationResponse.from(station));
        }
        return lineResponse;
    }
}
