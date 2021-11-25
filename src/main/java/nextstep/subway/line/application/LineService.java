package nextstep.subway.line.application;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    public static final String THE_REQUESTED_INFORMATION_DOES_NOT_EXIST = "The requested information does not exist.";
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        Station upStation = stationRepository.findById(request.getUpStationId()).get();
        Station downStation = stationRepository.findById(request.getDownStationId()).get();
        upStation.setLine(persistLine);
        downStation.setLine(persistLine);
        sectionRepository.save(new Section(persistLine, upStation, downStation, request.getDistance()));
        List<Station> stations = stationRepository.findByLine(persistLine);
        return LineResponse.from(persistLine, stations);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.from(lines);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findByLineId(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        List<Station> stations = stationRepository.findByLine(line);
        return LineResponse.from(line, stations);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.from(line);
    }
}
