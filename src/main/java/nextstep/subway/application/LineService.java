package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Station upStation = stationService.findStationById(lineRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationService.findStationById(lineRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);
        line.addStation(upStation);
        line.addStation(downStation);
        line = lineRepository.save(line);
        return LineResponse.of(line);
    }
}
