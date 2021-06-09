package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStaion = findStation(request.getUpStationId());
        Station downStaion = findStation(request.getDownStationId());
        Line line = request.toLine(upStaion, downStaion);

        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return lineRepository.findById(id).map(LineResponse::of).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Station findStation(Long id) {
        return stationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void updateLine(LineRequest lineRequest, Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Station upStaion = findStation(lineRequest.getUpStationId());
        Station downStaion = findStation(lineRequest.getDownStationId());
        findedLine.update(lineRequest.toLine(upStaion, downStaion));
    }

    public void deleteLine(Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(findedLine);
    }
}
