package nextstep.subway.line.application;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        return LineResponse.of(lineRepository.save(request.toLine(upStation, downStation)));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("`Line` " + id + " 는 존재하지 않습니다."));
    }

    public LineResponse findById(long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new CustomException("`Line`의 엔티티가 존재하지 않습니다."));
    }

    public LineResponse updateLine(long id, LineRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new CustomException("`Line`의 엔티티가 존재하지 않습니다."));
        line.update(request.getName(), request.getColor());
        return LineResponse.of(line);
    }

    public void deleteById(long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(long id, SectionRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        findLineById(id).addSection(upStation, downStation, request.getDistance());
    }
}
