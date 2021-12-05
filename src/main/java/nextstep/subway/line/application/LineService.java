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

        Station persistUpStation = findStation(request.getUpStationId());
        Station persistDownStation = findStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(persistUpStation, persistDownStation, request.getDistance()));
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

        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());

        line.update(lineRequest.toLine(upStation, downStation, lineRequest.getDistance()));

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

        Section section = sectionRequest.toSection(line, persistUpStation, persistDownStation, sectionRequest.getDistance());
        line.addToSections(section);

        return LineResponse.of(line);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
    }
}
