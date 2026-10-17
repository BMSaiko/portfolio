SELECT
    g."name" AS "Gauge",
    g.width AS "Gauge Width",
    COUNT(ls.line_segment_id) AS "Number of Segments"
FROM
    Gauge g
JOIN
    "Line" l ON g.gauge_id = l.gauge_id
JOIN
    LineSegment ls ON l.line_id = ls.line_id
GROUP BY
    g."name", g.width
ORDER BY
    g.width;