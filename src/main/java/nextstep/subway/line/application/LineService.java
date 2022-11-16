package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.ErrorMessageConstant;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).get();

        Line savedLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(lineRepository.findById(id).get());
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) throws RuntimeException {
        Line line = lineRepository.findById(id).orElseThrow(() -> new RuntimeException(ErrorMessageConstant.NOT_EXISTS_LINE));
        line.updateInfo(line.of(lineRequest));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
