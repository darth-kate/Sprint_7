import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetOrderSerial {
    private List<OrdersSerial> orders;
    private PageInfoSerial pageInfo;
    private List<AvailableStationSerial> availableStations;
}
