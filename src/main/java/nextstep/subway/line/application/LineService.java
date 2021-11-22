package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        checkDuplicatedName(request.getName());
        
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        
        Line line = request.toLine();
        line.addSection(upStation, downStation, request.getDistance());
        
        return LineResponse.of(lineRepository.save(request.toLine()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public LineResponse find(Long id) {
        return LineResponse.of(findById(id));
    }
    
    public void update(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.toLine());
    }
    
    public void delete(Long id) {
        Line line = findById(id);
        lineRepository.delete(line);
    }
    
    @Transactional(readOnly = true)
    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 노선이 없습니다."));
    }
    
    private void checkDuplicatedName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new IllegalArgumentException(String.format("라인 이름(%d)이 중복되었습니다.", name));
        }
    }
}
