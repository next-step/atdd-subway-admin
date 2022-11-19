package nextstep.subway.domain;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;

    protected Station() { }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> toMapForOpen() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("createdDate", getCreatedDate());
        map.put("modifiedDate", getModifiedDate());
        return map;
    }
}
