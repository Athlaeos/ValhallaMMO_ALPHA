package me.athlaeos.valhallammo.utility;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ShapeUtils {
    public static Collection<Location> getCubeWithLines(Location center, int lineDensity, double radius){
        Collection<Location> square = new HashSet<>();

        Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()-radius);
        Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()+radius);
        Location p3 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()-radius);
        Location p4 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()+radius);
        Location p5 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()-radius);
        Location p6 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()+radius);
        Location p7 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()-radius);
        Location p8 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()+radius);

        square.addAll(getPointsInLine(p1, p2, lineDensity));
        square.addAll(getPointsInLine(p1, p3, lineDensity));
        square.addAll(getPointsInLine(p2, p4, lineDensity));
        square.addAll(getPointsInLine(p3, p4, lineDensity));
        square.addAll(getPointsInLine(p5, p6, lineDensity));
        square.addAll(getPointsInLine(p5, p7, lineDensity));
        square.addAll(getPointsInLine(p6, p8, lineDensity));
        square.addAll(getPointsInLine(p7, p8, lineDensity));
        square.addAll(getPointsInLine(p1, p5, lineDensity));
        square.addAll(getPointsInLine(p2, p6, lineDensity));
        square.addAll(getPointsInLine(p3, p7, lineDensity));
        square.addAll(getPointsInLine(p4, p8, lineDensity));

        return square;
    }

    public static void drawLine(Location startingLocation, Vector direction, double length, double space){
        if (startingLocation.getWorld() == null) return;
        for (double i = 1; i <= length; i += space) {
            direction.multiply(i);
            startingLocation.add(direction);
            startingLocation.getWorld().spawnParticle(Particle.FLAME, startingLocation, 0, 0, 0, 0);
            startingLocation.subtract(direction);
            direction.normalize();
        }
    }

    public static void outlineBlock(Block b, int lineDensity, float particleSize, int red, int green, int blue){
        Particle.DustOptions data = new Particle.DustOptions(Color.fromRGB(red, green, blue), particleSize);
        for (Location point : ShapeUtils.getCubeWithLines(b.getLocation().clone().add(0.5, 0.5, 0.5), lineDensity, 0.5)){
            b.getWorld().spawnParticle(Particle.REDSTONE, point, 0, data);
        }
    }

    public static List<Location> getSquareWithLines(Location center, int lineDensity, double radius){
        List<Location> square = new ArrayList<>();

        Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()-radius);
        Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()+radius);
        Location p3 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()-radius);
        Location p4 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()+radius);

        square.addAll(getPointsInLine(p1, p2, lineDensity));
        square.addAll(getPointsInLine(p1, p3, lineDensity));
        square.addAll(getPointsInLine(p2, p4, lineDensity));
        square.addAll(getPointsInLine(p3, p4, lineDensity));

        return square;
    }

    public static List<Location> getPointsInLine(Location point1, Location point2, int amount){
        double xStep = (point1.getX() - point2.getX()) / amount;
        double yStep = (point1.getY() - point2.getY()) / amount;
        double zStep = (point1.getZ() - point2.getZ()) / amount;
        List<Location> points = new ArrayList<>();
        for (int i = 0; i < amount + 1; i++){
            points.add(new Location(
                    point1.getWorld(),
                    point1.getX() - xStep * i,
                    point1.getY() - yStep * i,
                    point1.getZ() - zStep * i));
        }
        return points;
    }

    public static Collection<Location> transformPoints(Location center, Collection<Location> points, double yaw, double pitch, double roll, double scale) {
        // Convert to radians
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);
        Collection<Location> list = new HashSet<>();

        // Store the values so we don't have to calculate them again for every single point.
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);
        double x, bx, y, by, z, bz;

        for (Location point : points) {
            x = point.getX() - center.getX();
            bx = x;
            y = point.getY() - center.getY();
            by = y;
            z = point.getZ() - center.getZ();
            bz = z;
            x = ((x*cy-bz*sy)*cr+by*sr)*scale;
            y = ((y*cp+bz*sp)*cr-bx*sr)*scale;
            z = ((z*cp-by*sp)*cy+bx*sy)*scale;
            list.add(new Location(point.getWorld(), (center.getX()+x), (center.getY()+y), (center.getZ()+z)));
        }
        return list;
    }

    public static void transformExistingPoints(Location center, List<Location> points, double yaw, double pitch, double roll, double scale) {
        // Convert to radians
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);

        // Store the values so we don't have to calculate them again for every single point.
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);
        double x, bx, y, by, z, bz;

        for (Location point : points) {
            x = point.getX() - center.getX();
            bx = x;
            y = point.getY() - center.getY();
            by = y;
            z = point.getZ() - center.getZ();
            bz = z;
            x = ((x * cy - bz * sy) * cr + by * sr) * scale;
            y = ((y * cp + bz * sp) * cr - bx * sr) * scale;
            z = ((z * cp - by * sp) * cy + bx * sy) * scale;
            point.setX((center.getX() + x));
            point.setY((center.getY() + y));
            point.setZ((center.getZ() + z));
        }
    }

    public static void transformPointsAroundOrigin(List<Location> points, double yaw, double pitch, double roll){
        double radsYaw = Math.toRadians(yaw);
        double radsPitch = Math.toRadians(yaw);
        double radsRoll = Math.toRadians(yaw);
        double sy = Math.sin(radsYaw);
        double cy = Math.cos(radsYaw);
        double sp = Math.sin(radsPitch);
        double cp = Math.cos(radsPitch);
        double sr = Math.sin(radsRoll);
        double cr = Math.cos(radsRoll);

        for (Location p : points){
            // Z rotation
            double x = p.getX();
            double y = p.getY();
            double z = p.getZ();
            p.setX(x * cy - y * sy);
            p.setY(y * cy + x * sy);
            y = p.getY();
            z = p.getZ();
            p.setY(y * cp - z * sp);
            p.setZ(z * cp + y * sp);
            x = p.getX();
            z = p.getZ();
            p.setX(x * cr - z * sr);
            p.setZ(z * cr + x * sr);
        }
    }

    public static void rotateZ3D(Collection<Location> points, double angle) {
        double rads = Math.toRadians(angle);
        double sinTheta = Math.sin(rads);
        double cosTheta = Math.sin(rads);

        for (Location l : points){
            double x = l.getX();
            double y = l.getY();
            l.setX(x * cosTheta - y * sinTheta);
            l.setY(y * cosTheta + x * sinTheta);
        }
    }

    public static void rotateX3D(Collection<Location> points, double angle) {
        double rads = Math.toRadians(angle);
        double sinTheta = Math.sin(rads);
        double cosTheta = Math.sin(rads);

        for (Location l : points){
            double y = l.getY();
            double z = l.getZ();
            l.setY(y * cosTheta - z * sinTheta);
            l.setZ(z * cosTheta + y * sinTheta);
        }
    }

    public static void rotateY3D(Collection<Location> points, double angle) {
        double rads = Math.toRadians(angle);
        double sinTheta = Math.sin(rads);
        double cosTheta = Math.sin(rads);

        for (Location l : points){
            double x = l.getX();
            double z = l.getZ();
            l.setX(x * cosTheta + z * sinTheta);
            l.setZ(z * cosTheta - x * sinTheta);
        }
    }
    @SafeVarargs
    public static void transformExistingPoints(Location center, double yaw, double pitch, double roll, double scale, List<Location>... collections) {
        // Convert to radians
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);

        // Store the values so we don't have to calculate them again for every single point.
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);
        double x, bx, y, by, z, bz;

        for (List<Location> points : collections){
            for (Location point : points) {
                x = point.getX() - center.getX();
                bx = x;
                y = point.getY() - center.getY();
                by = y;
                z = point.getZ() - center.getZ();
                bz = z;
                x = ((x * cy - bz * sy) * cr + by * sr) * scale;
                y = ((y * cp + bz * sp) * cr - bx * sr) * scale;
                z = ((z * cp - by * sp) * cy + bx * sy) * scale;
                point.setX((center.getX() + x));
                point.setY((center.getY() + y));
                point.setZ((center.getZ() + z));
            }
        }
    }

    public static Collection<Location> transformPointsPredefined(
            Location center, Collection<Location> points,
            double cp, double sp,
            double cy, double sy,
            double cr, double sr, double scale
    ) {
        Collection<Location> list = new HashSet<>();

        double x, bx, y, by, z, bz;
        for (Location point : points) {
            x = point.getX() - center.getX();
            bx = x;
            y = point.getY() - center.getY();
            by = y;
            z = point.getZ() - center.getZ();
            bz = z;
            x = ((x*cy-bz*sy)*cr+by*sr)*scale;
            y = ((y*cp+bz*sp)*cr-bx*sr)*scale;
            z = ((z*cp-by*sp)*cy+bx*sy)*scale;
            list.add(new Location(point.getWorld(), (center.getX()+x), (center.getY()+y), (center.getZ()+z)));
        }
        return list;
    }

    public static Collection<Location> getRandomPointsInCircle(Location center, double radius, int amount)
    {
        World world = center.getWorld();
        Collection<Location> locations = new HashSet<>();
        for(int i = 1;i < amount + 1; i++)
        {
            double theta = Utils.getRandom().nextDouble() * 2 * Math.PI;
            double x = (center.getX() + (radius * Math.cos(theta)));
            double z = (center.getZ() + (radius * Math.sin(theta)));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public static Collection<Location> getEvenCircle(Location center, double radius, int amount, double addAngle){
        World world = center.getWorld();
        Collection<Location> locations = new HashSet<>();
        for (double i = 0; i < amount; ++i) {
            double angle = Math.toRadians(((i / amount) * 360d)) + Math.toRadians((addAngle * 360d));
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;

            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
}
