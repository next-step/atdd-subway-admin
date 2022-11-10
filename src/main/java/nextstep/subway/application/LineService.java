package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

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
        Line persistLine = lineRepository.save(Line.of(lineRequest));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(RuntimeException::new);
        upStation.updateLine(persistLine);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(RuntimeException::new);
        downStation.updateLine(persistLine);
        return LineResponse.of(persistLine, upStation, downStation);
    }

    public List<LineResponse> findAllLines() {
        List<Line> allLines = lineRepository.findAllLines();
        return allLines.stream()
            .map(line -> LineResponse.of(line, line.getUpStation(), line.getDownStation()))
            .collect(Collectors.toList());
    }
}
