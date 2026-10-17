# USEI06 - Station Search Operations

## Overview

This user story implements multiple search operations for finding railway stations using different criteria: timezone
group, exact coordinates, and country. 

## Navigation

- [Requirements Engineering](01.requirements-engineering/USEI06-requirements.md)
- [Analysis](02.analysis/USEI06-analysis.md)
- [Design](03.design/USEI06-design.md)
- [Tests and Implementation](04.tests-and-implementation/USEI06-tests-and-implementation.md)

## Complexity Analysis

- Timezone group search: O(n) where n = number of timezones
- Coordinate search: O(m) where m = total stations
- Country search: O(n + k) where n = timezones, k = stations per timezone
