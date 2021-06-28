package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

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
        Station upStation = stationRepository.getById(request.getUpStationId());
        Station downStation = stationRepository.getById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public LineResponse getLineById(Long id) {
        return LineResponse.from(lineRepository.getById(id));
    }

    public LinesResponse getLines(LineRequest lineRequest) {
        return LinesResponse.from(lineRepository.findByNameContainingAndColorContaining(lineRequest.getName(), lineRequest.getColor()));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line updatingLine = lineRepository.getById(id);
        updatingLine.update(lineRequest.getName(), lineRequest.getColor());

        return LineResponse.from(lineRepository.save(updatingLine));
    }

    public void deleteLine(Long id) {
        Line deletingLine = lineRepository.getById(id);
        lineRepository.delete(deletingLine);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line foundLine = lineRepository.getById(lineId);
        Station upStation = stationRepository.getById(sectionRequest.getUpStationId());
        Station downStation = stationRepository.getById(sectionRequest.getDownStationId());
        foundLine.addSection(new Section(foundLine, upStation, downStation, new Distance(sectionRequest.getDistance())));
        return LineResponse.from(lineRepository.save(foundLine));
    }
}
