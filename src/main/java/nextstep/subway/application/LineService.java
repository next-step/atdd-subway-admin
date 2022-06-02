package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);

        Line persistLine = lineRepository.save(lineRequest.toLine());
        persistLine.initStation(upStation, downStation);

        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineUpdateRequest request) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        persistLine.updateLine(request.getName(), request.getColor());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line persisLine = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(persisLine);
    }

    @Transactional(readOnly = true)
    public LineResponses findAllLines() {
        List<Line> lines = lineRepository.findAll();
        List<LineResponse> list = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return new LineResponses(list);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long id, SectionRequest request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        Section section = new Section(request.getDistance(), upStation, downStation);
        line.insertSection(section);
    }
}
