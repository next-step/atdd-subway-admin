package nextstep.subway.application;

import nextstep.subway.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        Line line = findById(lineId);
        line.update(lineUpdateRequest);
    }

    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(NotFoundException::new);
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

	@Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = findById(lineId);
        return LineResponse.of(line);
    }

	@Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
    }
}
