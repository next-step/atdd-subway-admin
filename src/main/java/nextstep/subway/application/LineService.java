package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new IllegalStateException("상행종점역이 존재하지 않습니다."));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new IllegalStateException("하행종점역이 존재하지 않습니다."));

        Line savedLine = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance()));

        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> allLines = lineRepository.findAll();

        return allLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 노선입니다."));
        return LineResponse.of(line);
    }
}
