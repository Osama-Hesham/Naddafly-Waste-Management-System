package com.example.naddafly.Maps;

import java.util.List;

public class DirectionsResponse {
    List<Route> routes;
}

class Route {
    List<Leg> legs;
}

class Leg {
    Distance distance;
    Duration duration;
    List<Step> steps;
}

class Step {
    Polyline polyline;
}

class Polyline {
    String points;
}

class Distance {
    String text;
}

class Duration {
    String text;
}
