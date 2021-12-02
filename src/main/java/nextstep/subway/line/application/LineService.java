package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.exception.DuplicateLineNameException;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.line.dto.SectionRequest;
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
        if(lineRepository.existsByName(request.getName())){
            throw new DuplicateLineNameException(String.format("노선 이름이 이미 존재합니다.[%s]", request.getName()));
        }
        Line persistLine = lineRepository.save(lineOf(request));
        return LineResponse.of(persistLine);
    }

    private Line lineOf(LineRequest request) {

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Section section = Section.of(upStation, downStation, request.getDistance());

        Line line = request.toLine();
        line.addSection(section);
        return line;
    }

    @Transactional(readOnly = true)
    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(
                () -> new NotFoundStationException(String.format("존재하지 않는 역입니다.[stationId: %s]", stationId))
        );
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> findLines = lineRepository.findAll();

        return findLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new NotFoundLineException(String.format("존재하지 않는 노선입니다.[lineId: %s]", lineId))
        );
        return LineResponse.of(line);
    }

    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new NotFoundLineException(String.format("존재하지 않는 노선입니다.[lineId: %s]", lineId))
        );
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new NotFoundLineException(String.format("존재하지 않는 노선입니다.[lineId: %s]", lineId))
        );

        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        line.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
    }
}