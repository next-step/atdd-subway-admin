package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NotFoundStationException;
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

    public LineResponse saveLine(LineRequest request) {
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NotFoundStationException::new);
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(NotFoundStationException::new);

        Line line = new Line(request.getName(), request.getColor());
        line.addSection(new Section(upStation, downStation, request.getDistance()));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        return lineRepository
                .findById(id)
                .map(LineResponse::of)
                .orElseThrow(NotFoundLineException::new)
                ;
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        lineRepository.delete(line);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(NotFoundStationException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(NotFoundStationException::new);
        line.addSection(new Section(upStation, downStation, sectionRequest.getDistance()));
        return LineResponse.of(line);
    }

    public void deleteSection(Long id, Long stationId) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundLineException::new);
        Station findStation = line.getStations().stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(NotFoundStationException::new);
    }
}
