package nextstep.subway.line.application;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
    private LineStationRepository lineStationRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository,
                       LineStationRepository lineStationRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        LineStation upStation =  LineStation.ofFirst(request.getUpStationId(), request.getDistance());
        LineStation downStation = LineStation.of(request.getUpStationId(), request.getDownStationId(), request.getDistance());
        persistLine.addLineStation(upStation);
        persistLine.addLineStation(downStation);
        lineStationRepository.saveAll(persistLine.getLineStationList());
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

    public LineResponse updateLine(long id, LineRequest request) {
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
