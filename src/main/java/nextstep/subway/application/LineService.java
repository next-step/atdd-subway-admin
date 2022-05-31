package nextstep.subway.application;

import java.util.List;
import static java.util.stream.Collectors.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.factory.LineFactory;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.getById(lineRequest.getUpStationId());
        Station downStation = stationRepository.getById(lineRequest.getDownStationId());
        Line newLine = LineFactory.createNewLine(lineRequest.getName(), lineRequest.getColor(), upStation, downStation);
        return LineResponse.of(lineRepository.save(newLine));
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll().stream().map(LineResponse::of).collect(toList());
    }

    public LineResponse getLine(Long lineId) {
        return LineResponse.of(lineRepository.getById(lineId));
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.getById(lineId);
        line.updateLine(lineRequest.getName(),lineRequest.getColor());
        lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
