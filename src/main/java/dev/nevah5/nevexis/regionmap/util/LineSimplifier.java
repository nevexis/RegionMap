package dev.nevah5.nevexis.regionmap.util;

import com.flowpowered.math.vector.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class LineSimplifier {

    public static void removeIntermediatePoints(List<Vector2d> points) {
        points.forEach(System.out::println);
        if (points.size() <= 2) {
            return;
        }

        List<Vector2d> simplified = new ArrayList<>();
        simplified.add(points.get(0));
        simplified.add(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            simplified.add(points.get(i));
            // Check if the last three points are collinear and remove the middle one if so
            while (simplified.size() >= 3) {
                Vector2d a = simplified.get(simplified.size() - 3);
                Vector2d b = simplified.get(simplified.size() - 2);
                Vector2d c = simplified.get(simplified.size() - 1);

                if (areCollinear(a, b, c)) {
                    simplified.remove(simplified.size() - 2);
                } else {
                    break;
                }
            }
        }

        // Update the original list
        points.clear();
        points.addAll(simplified);
    }

    private static boolean areCollinear(Vector2d a, Vector2d b, Vector2d c) {
        // Handle vertical line (same x for all points)
        if (a.getX() == b.getX() && b.getX() == c.getX()) {
            return true;
        }
        // Handle horizontal line (same y for all points)
        if (a.getY() == b.getY() && b.getY() == c.getY()) {
            return true;
        }
        // General collinearity check with adjusted epsilon
        double area = (b.getX() - a.getX()) * (c.getY() - a.getY())
                - (b.getY() - a.getY()) * (c.getX() - a.getX());
        return Math.abs(area) < 1e-6; // Use a larger epsilon
    }

    public static void removeDuplicates(List<Vector2d> points) {
        if (points.size() <= 1) {
            return;
        }

        List<Vector2d> uniquePoints = new ArrayList<>();
        uniquePoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            Vector2d current = points.get(i);
            Vector2d lastUnique = uniquePoints.get(uniquePoints.size() - 1);
            if (!current.equals(lastUnique)) {
                uniquePoints.add(current);
            }
        }

        points.clear();
        points.addAll(uniquePoints);
    }
}
