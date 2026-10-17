| Concept (A)         |     Association     |     Concept (B) |
|---------------------|:-------------------:|----------------:|
| RailwayLine         |      contains       |     LineSegment |
| Station             |      connects       |     LineSegment |
| RailwayLine         |       serves        |         Station |
| RailwayLine         |     is owned by     |       LineOwner |
| LineSegment         |      connects       |         Station |
| LineSegment         |  is maintained by   |       LineOwner |
| LineSegment         |         has         |           Gauge |
| Train               |        uses         |      Locomotive |
| Train               |        pulls        |           Wagon |
| Train               |   is operated by    | RailwayOperator |
| Wagon               |       carries       |             Box |
| Wagon               |     is owned by     | RailwayOperator |
| Route               |      schedules      |           Train |
| Route               | defines trajectory  |            Path |
| Path                | composed of ordered |     PathSegment |
| PathSegment         |         at          |         Station |
| Freight             |     transports      |           Wagon |
| Freight             |       follows       |           Route |
| Order               |      generates      |     PickingPlan |
| Order               |      contains       |       OrderLine |
| OrderLine           |    fulfilled by     |             Box |
| Box                 |      stored in      |             Bay |
| Box                 |      loaded on      |           Wagon |
| Box                 |      contains       |            Item |
| Bay                 |    accessed via     |         Trolley |
| Bay                 |     located in      |           Aisle |
| Aisle               |       part of       |       Warehouse |
| Return              |       returns       |             Box |
| Return              |    processed by     | QualityOperator |
| PickingPlan         |        uses         |         Trolley |
| PickingPlan         |     assigned to     |          Picker |
| PickingPlan         |     created by      |         Planner |
| TrainDriver         |      operates       |           Train |
| StationManager      |       manages       |         Station |
| StationStoreManager |       manages       |       Warehouse |
| FreightManager      |      operates       |         Freight |
| Planner             |        plans        |           Route |
| RailwayOperator     |      operates       |     RailwayLine |
| RailwayOperator     |      maintains      |      Locomotive |
| RailwayOperator     |      maintains      |           Wagon |
| Allocation          |   allocates from    |             Box |
| Allocation          |      fulfills       |       OrderLine |
| LineSegment         |  is maintained by   |       LineOwner |
| Locomotive          |     is of type      |  LocomotiveType |
| Wagon               |     is of type      |       WagonType |
| Locomotive          |         has         |           Bogie |
| SKU                 |     identifies      |            Item |
| Box                 |      contains       |             SKU |
| OrderLine           |      requests       |             SKU |
| Return              |      involves       |             SKU |                  
| LineSegment         |         has         |           Gauge |
| Locomotive          |      supports       |           Gauge |
| Wagon               |         has         |           Gauge |
| Path                |      contains       |     PathSegment |                    
| PathSegment         |   passes through    |         Station |
| Train               |       records       |    TrainPassage |
| TrainPassage        |         at          |         Station |
| Route               |       follows       |            Path |                    
| Crossing            |      involves       |           Train |
| Crossing            |      occurs at      |         Station |
| Route               |        uses         |     LineSegment |
| Train               |  follows scheduled  |           Route |                     
