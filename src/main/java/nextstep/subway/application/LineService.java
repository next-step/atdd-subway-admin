package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.AddSectionRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);
        line.addSection(upStation, downStation, lineRequest.getDistance());
        return LineResponse.of(lineRepository.save(line));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAllWithSections().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public Optional<Line> findLineById(Long id) {
        return lineRepository.findByIdWithSections(id);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest request) {
        lineRepository.findById(id).ifPresent(line -> line.modifyBy(request));
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long id, AddSectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);
        lineRepository.findByIdWithSections(id).ifPresent(line -> line.addSection(upStation, downStation, sectionRequest.getDistance()));
    }
}
