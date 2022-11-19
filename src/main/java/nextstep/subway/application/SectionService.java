package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionListResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public void createSection(Long lineId, SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        Line line = getLine(lineId);
        checkRequestSectionIsLineEndStations(upStation, downStation, line);
        addRequestSection(upStation, downStation,request.getDistance(), line);
        lineRepository.save(line);
    }

    private void addRequestSection(Station upStation, Station downStation,Long distance, Line line) {
        boolean added = addSection(line, upStation, downStation, distance);
        if(!added){
            throw new IllegalArgumentException();
        }
    }

    private void checkRequestSectionIsLineEndStations(Station upStation, Station downStation, Line line) {
        boolean hasSame = line.hasSameEndStations(upStation, downStation);
        if(hasSame){
            throw new IllegalArgumentException();
        }
    }

    private boolean addSection(Line line, Station upStation, Station downStation, Long distance) {
        Sections sections = line.toSections();
        return sections.insertInsideFromUpStation(upStation, downStation, distance) ||
                sections.insertInsideFromDownStation(downStation, upStation, distance) ||
                sections.extendFromUpStation(upStation, downStation, distance) ||
                sections.extendFromDownStation(upStation, downStation, distance);
    }

    private Line getLine(Long lineId) {
        Optional<Line> lineOptional = lineRepository.findById(lineId);
        if (!lineOptional.isPresent()) {
            throw new IllegalArgumentException("요청하신 line은 존재하지 않습니다");
        }
        return lineOptional.get();
    }

    public SectionListResponse querySections(Long lineId) {
        return null;
    }
}
