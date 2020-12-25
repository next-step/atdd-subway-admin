package nextstep.subway.line.application;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.common.exception.NotExistsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        validate(request);
        Line persistLine = lineRepository.save(request.toLine());
        Station persistUpStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> createStationNotExistsException(request.getUpStationId()));
        Station persistDownStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> createStationNotExistsException(request.getUpStationId()));
        sectionRepository.save(new Section(persistLine, persistUpStation, persistDownStation, request.getDistance()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> createLineNotExistsException(id));
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> createLineNotExistsException(id));
        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public void deleteLine(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId);
        sectionRepository.deleteAll(sections);
        lineRepository.deleteById(lineId);
    }

    private NotExistsException createLineNotExistsException(Long id) {
        return new NotExistsException("line_id " + id + " is not exists.");
    }

    private NotExistsException createStationNotExistsException(Long id) {
        return new NotExistsException("station_id " + id + " is not exists.");
    }

    private void validate(LineRequest request) {
        if (request.getUpStationId().equals(request.getDownStationId())) {
            throw new BadRequestException("UpStation and DownStation should not be same");
        }
    }

}
