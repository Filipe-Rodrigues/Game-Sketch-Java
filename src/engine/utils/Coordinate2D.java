package engine.utils;

import static java.lang.Math.*;

public final class Coordinate2D {

    public static final Coordinate2D ORIGIN = new Coordinate2D(0, 0);
    
    public double x;
    public double y;

    public Coordinate2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate2D(Coordinate2D another) {
        copy(another);
    }
    
    public static Coordinate2D sum(Coordinate2D vector1, Coordinate2D vector2) {
        return new Coordinate2D(vector1.x + vector2.x, vector1.y + vector2.y);
    }
    
    public static Coordinate2D sub(Coordinate2D vector1, Coordinate2D vector2) {
        return new Coordinate2D(vector1.x - vector2.x, vector1.y - vector2.y);
    }
    
    public static Coordinate2D getVector(Coordinate2D pointA, Coordinate2D pointB) {
        Coordinate2D vector = new Coordinate2D(pointB.x - pointA.x, pointB.y - pointA.y);
        return vector;
    }

    public static double getDotProduct(Coordinate2D vector1, Coordinate2D vector2)  {
        return (vector1.x * vector2.x + vector1.y * vector2.y);
    }
    
    public static double getCrossProduct(Coordinate2D vector1, Coordinate2D vector2)  {
        return (vector1.x * vector2.y - vector1.y * vector2.x);
    }
    
    public static double computeAngle(Coordinate2D source, Coordinate2D target, Coordinate2D reference) {
        Coordinate2D vectorA = getVector(source, target);
        Coordinate2D vectorB = getVector(source, reference);
        double cosinusAlpha = getDotProduct(vectorA, vectorB) / (vectorA.getMagnitude() * vectorB.getMagnitude());
        double alpha = acos(cosinusAlpha);
        if (vectorB.x * vectorA.y - vectorA.x * vectorB.y < 0) {
            alpha = 2 * PI - alpha;
        }
        return alpha;
    }
    
    public static double getDistance(Coordinate2D coord1, Coordinate2D coord2) {
        if (coord1 != null && coord2 != null) {
            double sumSquared = (double) (Math.pow((coord1.x - coord2.x), 2)
                    + Math.pow((coord1.y - coord2.y), 2));
            return (double) Math.sqrt(sumSquared);
        }
        return Float.NaN;
    }

    public static double getScalarProjection(Coordinate2D vectorA, Coordinate2D vectorB) {
        return getDotProduct(vectorA, vectorB) / vectorB.getDotProduct(vectorB);
    }
    
    public static Coordinate2D getProjectionVector(Coordinate2D vectorA, Coordinate2D vectorB) {
        return vectorB.getScaled(getScalarProjection(vectorA, vectorB));
    }
    
    public static Coordinate2D getRejectionVector(Coordinate2D vectorA, Coordinate2D vectorB) {
        Coordinate2D projection = getProjectionVector(vectorA, vectorB);
        projection.x = vectorA.x - projection.x;
        projection.y = vectorA.y - projection.y;
        return projection;
    }
    
    public void copy(Coordinate2D another) {
        x = another.x;
        y = another.y;
    }
    
    public Coordinate2D getOrthogonal(Coordinate2D vector) {
        return new Coordinate2D(-vector.y, vector.x);
    }
    
    public double getDistance(Coordinate2D another) {
        return getDistance(this, another);
    }

    public double computeAngle(Coordinate2D target, Coordinate2D reference) {
        return computeAngle(this, target, reference);
    }

    public Coordinate2D getScaled(double scalar) {
        return new Coordinate2D(x * scalar, y * scalar);
    }
    
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }
    
    public double getMagnitude() {
        return sqrt(x * x + y * y);
    }

    public Coordinate2D getUnitVector() {
        return new Coordinate2D(x / getMagnitude(), y / getMagnitude());
    }
    
    public Coordinate2D getNormal() {
        return new Coordinate2D(y / getMagnitude(), - x / getMagnitude());
    }
    
    public double getDotProduct(Coordinate2D anotherVector)  {
        return getDotProduct(this, anotherVector);
    }
   
    public Coordinate2D getVector(Coordinate2D anotherPoint) {
        return getVector(this, anotherPoint);
    }
   
    public void sum(Coordinate2D point) {
        x += point.x;
        y += point.y;
    }
    
    public void sub(Coordinate2D point) {
        x -= point.x;
        y -= point.y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Coordinate2D) {
            Coordinate2D otherCoordinate = (Coordinate2D) other;
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
