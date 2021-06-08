package edu.ding.map;

import java.math.BigDecimal;
import java.security.SecureRandom;

public class TectonicCell {

    // halfedge data structure https://www.cs.princeton.edu/courses/archive/spring20/cos426/precepts/Precept-3.pdf
    private HalfEdge halfEdge;
    private int faceId;
    private TectonicCellNetwork network;
    private int parentId;
    private boolean isEdgeOfParentPlate;

    public TectonicCell(int faceId, HalfEdge he, TectonicCellNetwork network) {
        this.faceId = faceId;
        this.halfEdge = he;
        this.network = network;
        parentId = -1;
        isEdgeOfParentPlate = true;
    }

    public boolean isCellPartOfPlate(int cellId) {
        return network.getCell(cellId).getParentId() != -1; // && network.getCell(cellId).getParentId() != plateId
    }

    public BigDecimal[] getTopLeftCoordinate() {
        return new BigDecimal[]{halfEdge.getVertex().getRealX(), halfEdge.getVertex().getRealY()};
    }

    // assumes rectangular cell. averages top left and bottom right coordinates
    public BigDecimal[] getCenterCoordinate() {
        /*
        HalfEdge curr = halfEdge;
        BigDecimal[] tl = halfEdge.getVertex().getXY();
        BigDecimal[] br = halfEdge.getNextEdge().getNextEdge().getVertex().getXY();

        return new BigDecimal[]{tl[0].add(br[0]).divide(new BigDecimal(2)), tl[1].add(br[1]).divide(new BigDecimal(2))};*/

        return new BigDecimal[]{getTopLeftCoordinate()[0].add(network.getCellW()), getTopLeftCoordinate()[1].add(network.getCellH())};
    }

    public HalfEdge getHalfEdge() {
        return halfEdge;
    }

    public int getFaceId() {
        return faceId;
    }

    public void jostlePoints(double randomness) {
        HalfEdge curr = halfEdge;
        SecureRandom r = new SecureRandom();
        do  {
            Vertex v = curr.getVertex();
            v.setX(v.getRealX().add(new BigDecimal(r.nextDouble() * randomness - 1)));
            v.setY(v.getRealY().add(new BigDecimal(r.nextDouble() * randomness - 1)));
            curr = curr.getNextEdge();
        } while (curr != halfEdge);
    }

    public int[] getNeighborIds() {
        HalfEdge curr = halfEdge;
        int[] ids = new int[4];
        int i = 0;
        do  {
            //System.out.println("Curr: " + curr);
            //System.out.println(curr.getOppositeEdge());
            if (curr.getOppositeEdge() != null) { //todo issue where neighbors on bottom and top edges don't recognize each other
                int neighborCellId = curr.getOppositeEdge().getFaceId();
                ids[i] = neighborCellId;
                i++;
            }
            curr = curr.getNextEdge();
        } while (curr != halfEdge);

        return ids;
    }

    public void setParentId(int id) {
        parentId = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setEdgeOfParentPlate(boolean edgeOfParentPlate) {
        isEdgeOfParentPlate = edgeOfParentPlate;
    }

    public boolean isEdgeOfParentPlate() {
        return isEdgeOfParentPlate;
    }
}
