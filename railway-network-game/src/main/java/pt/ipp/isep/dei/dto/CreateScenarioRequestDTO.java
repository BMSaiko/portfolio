package pt.ipp.isep.dei.dto;

import java.util.Date;
import java.util.List;

public class CreateScenarioRequestDTO {
    public CreateScenarioRequestDTO() { throw new UnsupportedOperationException("Not implemented yet"); }
    public Long mapId;
    public String name;
    public Date startTime;
    public Date endTime;
    public List<Long> locomotiveTypeIds;
    public List<Long> industryTypeIds;
    public List<Object> historicalEvents;
}
