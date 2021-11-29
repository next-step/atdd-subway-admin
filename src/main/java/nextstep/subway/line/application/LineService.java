package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {

        Station persistUpStation = findStation(request.getUpStationId());
        Station persistDownStation = findStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(persistUpStation, persistDownStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(persistLine);
    }


    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persisLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());

        persisLine.update(lineRequest.toLine(upStation, downStation));

        return LineResponse.of(persisLine);
    }

    public Boolean deleteLine(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        lineRepository.delete(persistLine);
        return Boolean.TRUE;
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
    }
}
