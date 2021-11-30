package nextstep.subway.line.application;

import nextstep.subway.Exception.CannotSaveException;
import nextstep.subway.Exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        if (lineRepository.findByName(request.getName()) != null) {
            throw new CannotSaveException("요청 지하철 노선 이름이 중복임 : " + request.getName());
        }
        Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException("요청 노선이 존재하지 않음 : " + id));
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException("요청 노선이 존재하지 않음 : " + id));
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException("요청 노선이 존재하지 않음 : " + id));
        lineRepository.delete(line);
    }
}
