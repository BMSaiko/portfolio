flight TP004 {
  type: regular;
  leg {
    departure: OPO at 2025-06-01 10:00;
    arrival: MAD at 2025-06-01 12:00;
    route: OPO -> MAD;
    segment {
      mode: cruise;
      start: (41.2, -8.6, 9000 m);
      end: (40.4, -3.7, 9000 m);
    }
    fuel: 5000 kg;
  }
  leg {
    departure: LIS at 2025-06-01 14:00;  // LIS != MAD — erro R7
    arrival: BCN at 2025-06-01 15:30;
    route: LIS -> BCN;
    segment {
      mode: cruise;
      start: (38.7, -9.1, 9000 m);
      end: (41.3, 2.1, 9000 m);
    }
    fuel: 3000 kg;
  }
}