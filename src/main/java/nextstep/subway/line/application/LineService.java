package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Line line = request.toLine();
        Station upStation = stationService.findByIdOrElseThrow(request.getUpStationId());
        Station downStation = stationService.findByIdOrElseThrow(request.getDownStationId());
        Section section = new Section(upStation, downStation, request.getDistance());
        line.addSection(section);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse getLineById(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new));
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line sourceLine = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        sourceLine.update(lineRequest.toLine());
        Line persistLine = lineRepository.save(sourceLine);
        return LineResponse.of(persistLine);
    }

    public Line findByIdOrElseThrow(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 노선이 존재하지 않습니다"));
    }

    public void save(Line line) {
        lineRepository.save(line);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        // 해당 line에서 stationid가 들어간 section들을 잡아내서, 끼인 역이면 삭제 후 연결해주고 끝 역이면 그냥 section 삭제만.
        Line line = findByIdOrElseThrow(lineId);
        Station station = stationService.findByIdOrElseThrow(stationId);
        line.removeStation(station);
        lineRepository.save(line);
    }
}
