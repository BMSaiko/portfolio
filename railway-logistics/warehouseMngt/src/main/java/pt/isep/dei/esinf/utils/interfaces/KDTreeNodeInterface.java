package pt.isep.dei.esinf.utils.interfaces;

import java.awt.geom.Point2D;

/**
 * Interface for elements that can be stored in a KD-Tree
 */
public interface KDTreeNodeInterface {
    /**
     * Get the coordinates of this element
     * @return Point2D containing x (longitude) and y (latitude) coordinates
     */
    Point2D.Double getCoords();
}
