package engine.utils;

import static java.lang.Math.*;

public final class Coordinate2d {

    public static final Coordinate2d ORIGIN = new Coordinate2d(0, 0);
    
    public double x;
    public double y;

    public Coordinate2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate2d(Coordinate2d another) {
        copy(another);
    }
    
    public static Coordinate2d sum(Coordinate2d vector1, Coordinate2d vector2) {
        return new Coordinate2d(vector1.x + vector2.x, vector1.y + vector2.y);
    }
    
    public static Coordinate2d sub(Coordinate2d vector1, Coordinate2d vector2) {
        return new Coordinate2d(vector1.x - vector2.x, vector1.y - vector2.y);
    }
    
    public static Coordinate2d getVector(Coordinate2d pointA, Coordinate2d pointB) {
        Coordinate2d vector = new Coordinate2d(pointB.x - pointA.x, pointB.y - pointA.y);
        return vector;
    }

    public static double getDotProduct(Coordinate2d vector1, Coordinate2d vector2)  {
        return (vector1.x * vector2.x + vector1.y * vector2.y);
    }
    
    public static double getCrossProduct(Coordinate2d vector1, Coordinate2d vector2)  {
        return (vector1.x * vector2.y - vector1.y * vector2.x);
    }
    
    public static double computeAngle(Coordinate2d source, Coordinate2d target, Coordinate2d reference) {
        Coordinate2d vectorA = getVector(source, target);
        Coordinate2d vectorB = getVector(source, reference);
        double cosinusAlpha = getDotProduct(vectorA, vectorB) / (vectorA.getMagnitude() * vectorB.getMagnitude());
        double alpha = acos(cosinusAlpha);
        if (vectorB.x * vectorA.y - vectorA.x * vectorB.y < 0) {
            alpha = 2 * PI - alpha;
        }
        return alpha;
    }
    
    public static double getDistance(Coordinate2d coord1, Coordinate2d coord2) {
        if (coord1 != null && coord2 != null) {
            double sumSquared = (double) (Math.pow((coord1.x - coord2.x), 2)
                    + Math.pow((coord1.y - coord2.y), 2));
            return (double) Math.sqrt(sumSquared);
        }
        return Float.NaN;
    }

    public static double getScalarProjection(Coordinate2d vectorA, Coordinate2d vectorB) {
        return getDotProduct(vectorA, vectorB) / vectorB.getDotProduct(vectorB);
    }
    
    public static Coordinate2d getProjectionVector(Coordinate2d vectorA, Coordinate2d vectorB) {
        return vectorB.getScaled(getScalarProjection(vectorA, vectorB));
    }
    
    public static Coordinate2d getRejectionVector(Coordinate2d vectorA, Coordinate2d vectorB) {
        Coordinate2d projection = getProjectionVector(vectorA, vectorB);
        projection.x = vectorA.x - projection.x;
        projection.y = vectorA.y - projection.y;
        return projection;
    }
    
    public void copy(Coordinate2d another) {
        x = another.x;
        y = another.y;
    }
    
    public Coordinate2d getOrthogonal(Coordinate2d vector) {
        return new Coordinate2d(-vector.y, vector.x);
    }
    
    public double getDistance(Coordinate2d another) {
        return getDistance(this, another);
    }

    public double computeAngle(Coordinate2d target, Coordinate2d reference) {
        return computeAngle(this, target, reference);
    }

    public Coordinate2d getScaled(double scalar) {
        return new Coordinate2d(x * scalar, y * scalar);
    }
    
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }
    
    public double getMagnitude() {
        return sqrt(x * x + y * y);
    }

    public Coordinate2d getUnitVector() {
        return new Coordinate2d(x / getMagnitude(), y / getMagnitude());
    }
    
    public Coordinate2d getNormal() {
        return new Coordinate2d(y / getMagnitude(), - x / getMagnitude());
    }
    
    public double getDotProduct(Coordinate2d anotherVector)  {
        return getDotProduct(this, anotherVector);
    }
   
    public Coordinate2d getVector(Coordinate2d anotherPoint) {
        return getVector(this, anotherPoint);
    }
   
    public void sum(Coordinate2d point) {
        x += point.x;
        y += point.y;
    }
    
    public void sub(Coordinate2d point) {
        x -= point.x;
        y -= point.y;
    }
    
    public Coordinate2i toCoordinate2i() {
        return new Coordinate2i((int) x, (int) y);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Coordinate2d) {
            Coordinate2d otherCoordinate = (Coordinate2d) other;
            return x == otherCoordinate.x && y == otherCoordinate.y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

}
