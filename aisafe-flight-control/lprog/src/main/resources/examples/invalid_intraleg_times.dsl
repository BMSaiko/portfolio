flight TP006 {
  type: regular;
  leg {
    departure: OPO at 2025-06-01 14:00;  // parte depois de chegar — erro R6
    arrival: MAD at 2025-06-01 12:00;
    route: OPO -> MAD;
    segment {
      mode: cruise;
      start: (41.2, -8.6, 9000 m);
      end: (40.4, -3.7, 9000 m);
    }
    fuel: 5000 kg;
  }
}