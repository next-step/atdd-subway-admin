package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Optional<Line> result = lineRepository.findById(id);
        return result.map(LineResponse::of)
                .orElseGet(LineResponse::new);
    }

    @Transactional
    public ResponseEntity updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Optional<Line> line = lineRepository.findById(id);
        if (!line.isPresent()) {
            throw new NoSuchElementException("수정하고자 하는 지하철역을 찾을 수 없습니다.");
        }

        Line persistLine = line.get();
        persistLine.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("지하철역이 존재하지 않습니다."));
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("라인이 존재하지 않습니다."));
    }

    @Transactional
    public Line addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Station upStation = findStationById(sectionRequest.getUpStationId());

        if (line.getUpStation().getId().equals(downStation.getId())) {
            line.changeUpStation(upStation, sectionRequest.getDistance());
            return line;
        }

        if (line.getDownStation().getId().equals(upStation.getId())) {
            line.changeDownStation(downStation, sectionRequest.getDistance());
            return line;
        }

        Optional<Section> sameUpSection = line.getSections().stream()
                .filter(section -> !ObjectUtils.isEmpty(section.getUpStation()) && (section.getUpStation().getId().equals(upStation.getId())))
                .findFirst();

        if (sameUpSection.isPresent()) {
            addSectionBetweenExistingSection(sectionRequest, line, downStation, sameUpSection);
            return line;
        }

        return line;
    }

    private void addSectionBetweenExistingSection(SectionRequest sectionRequest, Line line, Station downStation, Optional<Section> existingUpSection) {
        long distance = sectionRequest.getDistance();
        if (existingUpSection.isPresent()) {
            Section existingSection = existingUpSection.get();
            Section newDownSection = new Section(downStation, existingSection.getDownStation()
                    , existingSection.getDistance() - distance);
            line.addSection(newDownSection);
            existingSection.setDownStation(downStation);
            existingSection.setDistance(distance);
        }
    }
}
