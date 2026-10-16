flight TP005 {
  type: charter;
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
    departure: MAD at 2025-06-01 14:00;
    arrival: OPO at 2025-06-01 16:00;  // OPO já foi visitado — erro R11
    route: MAD -> OPO;
    segment {
      mode: cruise;
      start: (40.4, -3.7, 9000 m);
      end: (41.2, -8.6, 9000 m);
    }
    fuel: 5000 kg;
  }
}