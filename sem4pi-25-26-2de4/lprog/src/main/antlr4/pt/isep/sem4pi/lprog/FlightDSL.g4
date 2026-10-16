grammar FlightDSL;

// ─────────────────────────────────────────────────────────────────────────────
// PARSER RULES
// ─────────────────────────────────────────────────────────────────────────────

// Root rule — one or more flight plan definitions in a single file
flightFile
    : flightPlan+ EOF
    ;

// Top-level flight block
flightPlan
    : FLIGHT identifier LBRACE
        typeDecl
        legBlock+
      RBRACE
    ;

// Flight type declaration
typeDecl
    : TYPE COLON flightType SEMICOLON
    ;

flightType
    : REGULAR
    | CHARTER
    ;

// Leg block — one leg of the flight
legBlock
    : LEG LBRACE
        departureBlock
        arrivalBlock
        routeBlock
        segmentBlock+
        fuelBlock
      RBRACE
    ;

// Departure declaration
departureBlock
    : DEPARTURE COLON airportCode (AT dateValue timeValue)? SEMICOLON
    ;

// Arrival declaration
arrivalBlock
    : ARRIVAL COLON airportCode (AT dateValue timeValue)? SEMICOLON
    ;

// Route declaration — origin -> destination
routeBlock
    : ROUTE COLON airportCode ARROW airportCode SEMICOLON
    ;

// Segment block — one flight segment (climb, cruise or descend)
segmentBlock
    : SEGMENT LBRACE
        modeDecl
        startCoord
        endCoord
        windDecl?
      RBRACE
    ;

// Segment mode
modeDecl
    : MODE COLON segmentMode SEMICOLON
    ;

segmentMode
    : CLIMB
    | CRUISE
    | DESCEND
    ;

// Start coordinate
startCoord
    : START COLON coordinate SEMICOLON
    ;

// End coordinate
endCoord
    : END COLON coordinate SEMICOLON
    ;

// Geographic coordinate — (latitude, longitude, altitude unit)
coordinate
    : LPAREN signedNumber COMMA signedNumber COMMA signedNumber altitudeUnit RPAREN
    ;

// Optional wind declaration
windDecl
    : WIND COLON signedNumber signedNumber speedUnit SEMICOLON
    ;

// Fuel declaration
fuelBlock
    : FUEL COLON signedNumber fuelUnit SEMICOLON
    ;

// Airport code — IATA (3 letters) or ICAO (4 letters)
airportCode
    : AIRPORT_CODE
    ;

// Signed numeric value (allows negative coordinates and descent rates)
signedNumber
    : MINUS? NUMBER
    ;

// A flight/leg identifier
identifier
    : ID
    ;

// Date value
dateValue
    : DATE
    ;

// Time value
timeValue
    : TIME
    ;

// Altitude units
altitudeUnit
    : M
    | KM
    | FT
    ;

// Speed units
speedUnit
    : MS
    | KNOT
    | KMH
    ;

// Fuel units
fuelUnit
    : KG
    | L
    ;


// ─────────────────────────────────────────────────────────────────────────────
// LEXER RULES
// ─────────────────────────────────────────────────────────────────────────────

// ── Keywords (case-insensitive) ──────────────────────────────────────────────

FLIGHT      : [Ff][Ll][Ii][Gg][Hh][Tt] ;
LEG         : [Ll][Ee][Gg] ;
TYPE        : [Tt][Yy][Pp][Ee] ;
REGULAR     : [Rr][Ee][Gg][Uu][Ll][Aa][Rr] ;
CHARTER     : [Cc][Hh][Aa][Rr][Tt][Ee][Rr] ;
DEPARTURE   : [Dd][Ee][Pp][Aa][Rr][Tt][Uu][Rr][Ee] ;
ARRIVAL     : [Aa][Rr][Rr][Ii][Vv][Aa][Ll] ;
ROUTE       : [Rr][Oo][Uu][Tt][Ee] ;
SEGMENT     : [Ss][Ee][Gg][Mm][Ee][Nn][Tt] ;
MODE        : [Mm][Oo][Dd][Ee] ;
CLIMB       : [Cc][Ll][Ii][Mm][Bb] ;
CRUISE      : [Cc][Rr][Uu][Ii][Ss][Ee] ;
DESCEND     : [Dd][Ee][Ss][Cc][Ee][Nn][Dd] ;
START       : [Ss][Tt][Aa][Rr][Tt] ;
END         : [Ee][Nn][Dd] ;
WIND        : [Ww][Ii][Nn][Dd] ;
FUEL        : [Ff][Uu][Ee][Ll] ;
AT          : [Aa][Tt] ;

// ── Units (case-sensitive — standard aviation notation) ──────────────────────

KG          : 'kg' ;
L           : 'l' ;
M           : 'm' ;
KM          : 'km' ;
FT          : 'ft' ;
MS          : 'm/s' ;
KNOT        : 'knot' ;
KMH         : 'km/h' ;

// ── Airport code — must come BEFORE ID rule ──────────────────────────────────
// Matches exactly 3 or 4 uppercase letters (IATA / ICAO)

AIRPORT_CODE : [A-Z][A-Z][A-Z][A-Z]?  ;

// ── General identifier — alphanumeric, starts with letter ────────────────────

ID          : [A-Za-z][A-Za-z0-9_]* ;

// ── Numeric literals ─────────────────────────────────────────────────────────

NUMBER      : [0-9]+ ('.' [0-9]+)? ;

// ── Date literal  YYYY-MM-DD ─────────────────────────────────────────────────

DATE        : [0-9][0-9][0-9][0-9] '-' [0-9][0-9] '-' [0-9][0-9] ;

// ── Time literal  HH:MM or HH:MM:SS ─────────────────────────────────────────

TIME        : [0-9][0-9] ':' [0-9][0-9] (':' [0-9][0-9])? ;

// ── Symbols ──────────────────────────────────────────────────────────────────

LBRACE      : '{' ;
RBRACE      : '}' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
COLON       : ':' ;
SEMICOLON   : ';' ;
COMMA       : ',' ;
ARROW       : '->' ;
MINUS       : '-' ;

// ── Whitespace and comments (skipped) ────────────────────────────────────────

WS              : [ \t\r\n]+           -> skip ;
LINE_COMMENT    : '//' ~[\r\n]*        -> skip ;
BLOCK_COMMENT   : '/*' .*? '*/'       -> skip ;