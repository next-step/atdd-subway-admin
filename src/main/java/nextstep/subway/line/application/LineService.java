package nextstep.subway.line.application;

import nextstep.subway.line.application.dto.AddSectionRequest;
import nextstep.subway.line.application.dto.LineUpdateRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.ExistDuplicatedNameException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validateSaveLine(request.getName());
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line persistLine = lineRepository.save(requestToLIne(request, upStation, downStation));
        return lineToResponse(persistLine);
    }

    private void validateSaveLine(String name) {
        if (lineRepository.existsByName(name)) {
            throw new ExistDuplicatedNameException(String.format("노선 이름이 이미 존재합니다.[%s]", name));
        }
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> lineToResponse(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = findLineById(lineId);

        return lineToResponse(line);
    }

    @Transactional
    public LineResponse update(LineUpdateRequest lineUpdateRequest) {
        Line line = findLineById(lineUpdateRequest.getId());
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());

        return lineToResponse(line);
    }

    @Transactional
    public void delete(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public Section addSection(AddSectionRequest addSectionRequest) {
        Line line = findLineById(addSectionRequest.getLineId());
        Station upStation = findStationById(addSectionRequest.getUpStationId());
        Station downStation = findStationById(addSectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, addSectionRequest.getDistance(), line);
        line.addSection(section);
        return section;
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("노선이 존재하지 않습니다."));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("역이 존재하지 않습니다."));
    }

    private Line requestToLIne(LineRequest request, Station upStation, Station downStation) {
        return new Line(request.getName(), request.getColor(), new Section(upStation, downStation, request.getDistance()));
    }

    private LineResponse lineToResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), createStationResponses(line));
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations()
                .stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }
}
