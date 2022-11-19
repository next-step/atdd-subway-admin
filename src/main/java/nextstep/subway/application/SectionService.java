package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
public class SectionService {
    private static final String MESSAGE_NEW_SECTION_IS_SAME_WITH_LINE = "노선 끝과 동일한 상/하행역을 가진 구간은 등록할 수 없습니다";
    private static final String MESSAGE_CAN_NOT_ADD_SECTION = "구간을 등록하지 못했습니다";
    private final LineService lineService;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineService lineService, LineRepository lineRepository, StationRepository stationRepository) {
        this.lineService = lineService;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public void createSection(Long lineId, SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        Line line = lineService.findLineOrThrowException(lineId);
        checkRequestSectionIsLineEndStations(upStation, downStation, line);
        addRequestSection(upStation, downStation,request.getDistance(), line);
        lineRepository.save(line);
    }

    private void addRequestSection(Station upStation, Station downStation,Long distance, Line line) {
        boolean added = addSection(line, upStation, downStation, distance);
        if(!added){
            throw new IllegalArgumentException(MESSAGE_CAN_NOT_ADD_SECTION);
        }
    }

    private void checkRequestSectionIsLineEndStations(Station upStation, Station downStation, Line line) {
        boolean hasSame = line.hasSameEndStations(upStation, downStation);
        if(hasSame){
            throw new IllegalArgumentException(MESSAGE_NEW_SECTION_IS_SAME_WITH_LINE);
        }
    }

    private boolean addSection(Line line, Station upStation, Station downStation, Long distance) {
        Sections sections = line.toSections();
        return sections.insertInsideFromUpStation(upStation, downStation, distance) ||
                sections.insertInsideFromDownStation(downStation, upStation, distance) ||
                sections.extendFromUpStation(upStation, downStation, distance) ||
                sections.extendFromDownStation(upStation, downStation, distance);
    }
}
