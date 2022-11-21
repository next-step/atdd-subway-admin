package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;
    private LineStationService lineStationService;

    public LineService(LineRepository lineRepository, StationService stationService,
                       LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequestToLine(lineRequest));
        lineStationService.saveLineStation(new LineStation(persistLine, persistLine.getDownStation()));
        lineStationService.saveLineStation(new LineStation(persistLine, persistLine.getUpStation()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line getLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(getLineById(id));
    }

    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
        line.updateNameAndColor(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineRepository.save(line);
    }

    public void deleteLineById(Long id) {
        lineStationService.deleteLineStationByLineId(id);
        lineRepository.deleteById(id);
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor(),
                stationService.getStationById(lineRequest.getUpStationId()),
                stationService.getStationById(lineRequest.getDownStationId()),
                lineRequest.getDistance());
    }

}
