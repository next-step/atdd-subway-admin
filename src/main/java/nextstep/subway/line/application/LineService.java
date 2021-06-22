package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineAndStationResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(NotFoundException::new);

        Line line = request.toLine();
        line.addSection(new Section(upStation, downStation, request.getDistance()));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public List<LineAndStationResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineAndStationResponse::of).collect(Collectors.toList());
    }

    public LineAndStationResponse findByLine(Long id) {
        return LineAndStationResponse.of(lineRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        lineRepository
            .findById(id)
            .ifPresent(line -> line.update(lineRequest.toLine()));
    }

    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(NotFoundException::new);
        line.addSection(new Section(upStation, downStation, request.getDistance()));
    }
}
