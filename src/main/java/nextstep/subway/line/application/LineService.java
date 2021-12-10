package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
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
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());

        Line line = request.toLine();
        Section section = request.toSection(line, upStation, downStation);
        line.addToSections(section);

        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(persistLine);
    }


    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public Boolean deleteLine(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        lineRepository.delete(persistLine);
        return Boolean.TRUE;
    }

    public LineResponse saveSectionToLine(Long lineId, SectionRequest sectionRequest) {

        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

        Station persistUpStation = findStation(sectionRequest.getUpStationId());
        Station persistDownStation = findStation(sectionRequest.getDownStationId());

        Section section = sectionRequest.toSection(line, persistUpStation, persistDownStation);
        line.addToSections(section);

        return LineResponse.of(line);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
    }
}
