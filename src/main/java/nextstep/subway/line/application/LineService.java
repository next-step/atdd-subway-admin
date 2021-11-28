package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NotFoundStationException;

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
        checkValidateLine(request);
        Line line = lineRepository.save(request.toLine());
        return LineResponse.of(line);
    }

    private void checkValidateLine(LineRequest request) {
        checkExistsStation(request.getUpStationId());
        checkExistsStation(request.getDownStationId());

        if (isDuplicate(request.getName())) {
            throw new LineDuplicateException();
        }
    }

    private void checkExistsStation(Long stationId) {
        stationRepository.findById(stationId)
            .orElseThrow(NotFoundStationException::new);
    }

    private boolean isDuplicate(String name) {
        return lineRepository.findByName(name).isPresent();
    }

    @Transactional()
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findId(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotFoundLineException::new);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotFoundLineException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

}
