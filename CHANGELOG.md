# Changelog

## v0.4 - 2019-05-04

### Added
- `scatter()` Spark helper for producing a matrix of scatter plots
- `Vega()` class so Vega plots can be generated and rendered as well as existing Vega-Lite plots

### Changed
- The `limit` parameter in the Spark plot helpers (e.g. `withData` and `hist`) has been moved to two Spark conf properties:
   - `spark.vegalite4s.limitCollect`: A boolean (default `true`) that configures whether Spark datasets should be limited
   - `spark.vegalite4s.maxRowsToCollect`: A long (default 10000) that dictates the limit for rows to collect
- The underlying Json schema produced before being rendered now uses a compact representation to reduce schema size

## v0.3 - 2019-03-24

### Added
- `.withLayer()` plot helper function for producing layered plot specs
- `.hist()` Spark helper for producing a matrix of histogram plots from a Dataset

### Changed
- Updated the default Vega-Lite version to v3

## v0.2 - 2019-03-11

### Fixed
- Null and Java BigInteger and BigDecimal datatype conversions

## v0.1 - 2019-02-18

### Added
- Initial release
