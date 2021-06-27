package nextstep.subway.line.application;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineCreateRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return toLineResponse(persistLine);
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(this::toLineResponse).collect(Collectors.toList());
    }

    public LineResponse getLine(long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
       return toLineResponse(line);
    }

    public LineResponse updateLine(long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        line.update(request.toLine());
        return toLineResponse(line);
    }

    public void delete(long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        lineRepository.delete(line);
    }

    private LineResponse toLineResponse(Line line){
        List<StationResponse> stationResponses = stationService.findByIds(line.getStationIds());
        return LineResponse.of(line,stationResponses);
    }

}
