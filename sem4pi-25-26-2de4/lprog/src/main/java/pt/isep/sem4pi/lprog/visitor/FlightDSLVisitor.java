package pt.isep.sem4pi.lprog.visitor;

import pt.isep.sem4pi.lprog.FlightDSLBaseVisitor;
import pt.isep.sem4pi.lprog.FlightDSLParser;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FlightDSLVisitor extends FlightDSLBaseVisitor<Object> {

    @Override
    public List<FlightPlan> visitFlightFile(FlightDSLParser.FlightFileContext ctx) {
        List<FlightPlan> plans = new ArrayList<>();
        for (FlightDSLParser.FlightPlanContext planCtx : ctx.flightPlan()) {
            plans.add((FlightPlan) visit(planCtx));
        }
        return plans;
    }

    @Override
    public FlightPlan visitFlightPlan(FlightDSLParser.FlightPlanContext ctx) {
        FlightPlan plan = new FlightPlan();
        plan.setLine(ctx.start.getLine());
        plan.setColumn(ctx.start.getCharPositionInLine());
        plan.setId(ctx.identifier().getText());
        plan.setType(ctx.typeDecl().flightType().getText());

        for (FlightDSLParser.LegBlockContext legCtx : ctx.legBlock()) {
            plan.addLeg((FlightLeg) visit(legCtx));
        }
        return plan;
    }

    @Override
    public FlightLeg visitLegBlock(FlightDSLParser.LegBlockContext ctx) {
        FlightLeg leg = new FlightLeg();
        leg.setDeparture((Departure) visit(ctx.departureBlock()));
        leg.setArrival((Arrival) visit(ctx.arrivalBlock()));
        leg.setRoute((Route) visit(ctx.routeBlock()));
        leg.setFuel((Fuel) visit(ctx.fuelBlock()));

        for (FlightDSLParser.SegmentBlockContext segCtx : ctx.segmentBlock()) {
            leg.addSegment((FlightSegment) visit(segCtx));
        }
        return leg;
    }

    @Override
    public Departure visitDepartureBlock(FlightDSLParser.DepartureBlockContext ctx) {
        LocalDateTime time = parseDateTime(ctx.dateValue(), ctx.timeValue());
        return new Departure(ctx.airportCode().getText(), time, ctx.start.getLine());
    }

    @Override
    public Arrival visitArrivalBlock(FlightDSLParser.ArrivalBlockContext ctx) {
        LocalDateTime time = parseDateTime(ctx.dateValue(), ctx.timeValue());
        return new Arrival(ctx.airportCode().getText(), time, ctx.start.getLine());
    }

    @Override
    public Route visitRouteBlock(FlightDSLParser.RouteBlockContext ctx) {
        return new Route(ctx.airportCode(0).getText(), ctx.airportCode(1).getText(), ctx.start.getLine());
    }

    @Override
    public FlightSegment visitSegmentBlock(FlightDSLParser.SegmentBlockContext ctx) {
        Coordinate start = (Coordinate) visit(ctx.startCoord().coordinate());
        Coordinate end = (Coordinate) visit(ctx.endCoord().coordinate());

        double windSpeed = 0.0;
        if (ctx.windDecl() != null) {
            windSpeed = Double.parseDouble(ctx.windDecl().signedNumber(1).getText());
        }

        return new FlightSegment(ctx.modeDecl().segmentMode().getText(), start, end, windSpeed, ctx.start.getLine());
    }

    @Override
    public Coordinate visitCoordinate(FlightDSLParser.CoordinateContext ctx) {
        return new Coordinate(
                Double.parseDouble(ctx.signedNumber(0).getText()),
                Double.parseDouble(ctx.signedNumber(1).getText()),
                Double.parseDouble(ctx.signedNumber(2).getText())
        );
    }

    @Override
    public Fuel visitFuelBlock(FlightDSLParser.FuelBlockContext ctx) {
        return new Fuel(
                Double.parseDouble(ctx.signedNumber().getText()),
                ctx.fuelUnit().getText(),
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
        );
    }

    // Helper para juntar Date e Time do ANTLR
    private LocalDateTime parseDateTime(FlightDSLParser.DateValueContext dateCtx, FlightDSLParser.TimeValueContext timeCtx) {
        if (dateCtx == null || timeCtx == null) return null;
        try {
            LocalDate date = LocalDate.parse(dateCtx.getText());
            // Acertar time (HH:MM vs HH:MM:SS)
            String timeStr = timeCtx.getText();
            if (timeStr.length() == 5) timeStr += ":00";
            LocalTime time = LocalTime.parse(timeStr);
            return LocalDateTime.of(date, time);
        } catch (Exception e) {
            return null; // As datas inválidas serão apanhadas pelo Semantic Validator
        }
    }
}