package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        sectionRepository.saveAll(persistLine.getSections());
        return LineResponse.of(persistLine, getStations(persistLine));
    }

    public List<LineResponse> getLines(){
        List<Line> lines = lineRepository.findAll();
        List<Long> stationIds = lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .map(Section::getId)
                .collect(Collectors.toList());
        Map<Long, StationResponse> stationMap = stationService.findByIds(stationIds)
                .stream()
                .collect(Collectors.toMap(StationResponse::getId, Function.identity()));
        return lines.stream().map(line -> LineResponse.of(line, getStations(line, stationMap))).collect(Collectors.toList());
    }

    public LineResponse getLine(long id){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        return LineResponse.of(line, getStations(line));
    }

    public LineResponse updateLine(long id, LineRequest request){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        line.update(request.toLine());
        return LineResponse.of(line, getStations(line));
    }

    public void delete(long id){
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 노선을 찾을 수 없습니다."));
        lineRepository.delete(line);
    }

    private List<StationResponse> getStations(Line line, Map<Long, StationResponse> stationMap){
        return line.getSections().stream()
                .map(section -> Arrays.asList(stationMap.get(section.getUpStationId()), stationMap.get(section.getDownStationId())))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<StationResponse> getStations(Line line){
        List<Long> stationIds = line.getSections().stream().map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId())).flatMap(Collection::stream).collect(Collectors.toList());
        return stationService.findByIds(stationIds);
    }
}
