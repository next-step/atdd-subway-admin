package nextstep.subway.lineStation.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.lineStation.dto.LineStationResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineStationService {
    private LineStationRepository lineStationRepository;

    public LineStationService(LineStationRepository lineStationRepository) {
        this.lineStationRepository = lineStationRepository;
    }

    public List<LineStationResponse> findAllLineStations() {
        List<LineStation> lineStations = lineStationRepository.findAll();
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }

    public List<LineStationResponse> findByStationId(Long id) {
        List<LineStation> lineStations = lineStationRepository.findByStationId(id);
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }

    public List<LineStationResponse> findByLineId(Long id) {
        List<LineStation> lineStations = lineStationRepository.findByLineId(id);
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }

    public LineStation findByLineIdAndStationId(Line line, Station station) {
        return lineStationRepository.findByLineIdAndStationId(line.getId(), station.getId()).orElse(new LineStation());
    }
}
