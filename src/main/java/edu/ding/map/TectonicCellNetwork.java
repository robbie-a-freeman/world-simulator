package edu.ding.map;

import edu.ding.eng.Location;

import java.awt.*;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class TectonicCellNetwork implements Drawable {

    private ConcurrentHashMap<BigDecimal, ConcurrentHashMap<BigDecimal, Vertex>> allVertices; // first col, then row
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, HalfEdge>> allHalfEdges; // first starting vert ID, then ending vert ID
    private ConcurrentHashMap<Integer, TectonicCell> allCells; // one half edge per face
    private ArrayList<TectonicPlate> plates;

    private BigDecimal cellW, cellH;

    public TectonicCellNetwork(BigDecimal cellW, BigDecimal cellH) {
        allVertices = new ConcurrentHashMap<>();
        allHalfEdges = new ConcurrentHashMap<>();
        allCells = new ConcurrentHashMap<>();
        plates = new ArrayList<>();
        this.cellH = cellH;
        this.cellW = cellW; // initial cell height and weight, should be uniform

        // add vertices of cells
        BigDecimal currX = new BigDecimal(0);
        BigDecimal currY = new BigDecimal(0);
        int i = 0;
        while (currY.doubleValue() < World.WORLD_SIZE[1] + 0.0001) { // double precision
            System.out.println(currY.doubleValue());
            Vertex v;
            if (currX.equals(new BigDecimal(500))) {
                v = new Vertex(i, new BigDecimal[]{currX, currY}, allVertices.get(new BigDecimal(0)).get(currY)); // create a virtual vertex
            } else {
                v = new Vertex(i, new BigDecimal[]{currX, currY});
            }
            ConcurrentHashMap<BigDecimal, Vertex> subMap = allVertices.get(currX);
            if (subMap == null) {
                subMap = new ConcurrentHashMap<>();
            }
            subMap.put(currY, v);
            allVertices.put(currX, subMap);
            i++;
            currX = currX.add(cellW);
            if (currX.doubleValue() >= World.WORLD_SIZE[0] + 0.0001) {
                currX = new BigDecimal(0);
                currY = currY.add(cellH);
            }
        }

        // overwrite the vertices that need to be virtual on the top and bottom edges
        for (int v = 0; v < allVertices.size() / 2; v++) {
            // top
            BigDecimal cellX = cellW.multiply(new BigDecimal(v));
            Vertex toVirtual = allVertices.get(cellX).get(new BigDecimal(0));
            BigDecimal linkedCellX = cellW.multiply(new BigDecimal(v)).add(new BigDecimal(allVertices.size() / 2).multiply(cellW));
            toVirtual = new Vertex(toVirtual.getId(), new BigDecimal[]{toVirtual.getX(), toVirtual.getY()}, allVertices.get(linkedCellX).get(new BigDecimal(0)));
            allVertices.get(cellX).put(new BigDecimal(0), toVirtual);

            // bottom
            toVirtual = allVertices.get(cellX).get(new BigDecimal(World.WORLD_SIZE[1]));
            toVirtual = new Vertex(toVirtual.getId(), new BigDecimal[]{toVirtual.getX(), toVirtual.getY()}, allVertices.get(linkedCellX).get(new BigDecimal(World.WORLD_SIZE[1])));
            allVertices.get(cellX).put(new BigDecimal(World.WORLD_SIZE[1]), toVirtual);
        }


        //if (currY.doubleValue() >= worldSize[1] - 0.0001)
        //   currY = new BigDecimal(0);

        // build halfEdges
        // iterate through vertices, build in groups of 4
        currX = new BigDecimal(0);
        currY = new BigDecimal(0);
        i = 0; // cell number (face)
        while (currY.doubleValue() < World.WORLD_SIZE[1] - 0.0001) { // double precision

            BigDecimal rightOffset = currX.add(cellW);
            BigDecimal bottomOffset = currY.add(cellH);
            if (bottomOffset.doubleValue() > World.WORLD_SIZE[1]) {
                bottomOffset = new BigDecimal(500);
                rightOffset = rightOffset.add(new BigDecimal(250)).add(cellW);
                if (rightOffset.compareTo(new BigDecimal(500)) > 0)
                    rightOffset = rightOffset.subtract(new BigDecimal(500));
            } else if (rightOffset.doubleValue() > World.WORLD_SIZE[0])
                rightOffset = new BigDecimal(0);

            Vertex tl = allVertices.get(currX).get(currY);
            Vertex bl = allVertices.get(currX).get(bottomOffset);
            Vertex br = allVertices.get(rightOffset).get(bottomOffset);
            Vertex tr = allVertices.get(rightOffset).get(currY);
            HalfEdge le = new HalfEdge(allHalfEdges.size(), tl, null, i, null);
            HalfEdge be = new HalfEdge(allHalfEdges.size() + 1, bl, null, i, null);
            HalfEdge re = new HalfEdge(allHalfEdges.size() + 2, br, null, i, null);
            HalfEdge te = new HalfEdge(allHalfEdges.size() + 3, tr, null, i, null);
            // set the next edge pointers
            le.setNextEdge(be);
            be.setNextEdge(re);
            re.setNextEdge(te);
            te.setNextEdge(le);
            // add them to the allVertices collection
            /*allHalfEdges.put(new Integer[]{tl.getId(), bl.getId()}, le);
            allHalfEdges.put(new Integer[]{bl.getId(), br.getId()}, be);
            allHalfEdges.put(new Integer[]{br.getId(), tr.getId()}, re);
            allHalfEdges.put(new Integer[]{tr.getId(), tl.getId()}, te);*/
            ConcurrentHashMap<Integer, HalfEdge> subMap = allHalfEdges.get(tl.getId());
            if (subMap == null) {
                subMap = new ConcurrentHashMap<>();
            }
            subMap.put(bl.getId(), le);
            allHalfEdges.put(tl.getId(), subMap);

            subMap = allHalfEdges.get(bl.getId());
            if (subMap == null) {
                subMap = new ConcurrentHashMap<>();
            }
            subMap.put(br.getId(), be);
            allHalfEdges.put(bl.getId(), subMap);

            subMap = allHalfEdges.get(br.getId());
            if (subMap == null) {
                subMap = new ConcurrentHashMap<>();
            }
            subMap.put(tr.getId(), re);
            allHalfEdges.put(br.getId(), subMap);

            subMap = allHalfEdges.get(tr.getId());
            if (subMap == null) {
                subMap = new ConcurrentHashMap<>();
            }
            subMap.put(tl.getId(), te);
            allHalfEdges.put(tr.getId(), subMap);

            currX = currX.add(cellW);
            i++;
            if (currX.doubleValue() >= World.WORLD_SIZE[0] + 0.0001) {
                currX = new BigDecimal(0);
                currY = currY.add(cellH);
            }

        }

        /*for (int i = 0; i < allVertices.size(); i++) {
            if (te.getVertex().getY().equals(new BigDecimal(0))) {
                HalfEdge oppHe = new HalfEdge(allHalfEdges.size(), allHalfEdges.get, te, -1, ); // these will probably never be used, nor should they be? todo
            }
        } */

        // set the opposite edge pointers, now that all edges are initialized
        // also initialize the tectonic cells
        for (ConcurrentHashMap<Integer, HalfEdge> el : allHalfEdges.values()) { // double precision
            for (HalfEdge e : el.values()){
                // case where top edge never gets opposites
                //if (e.getVertex().getX().equals(new BigDecimal(0)))
                    //e.setOppositeEdge(allHalfEdges.get(e.getNorthPoleEdge().getVertex().getId()).get(e.getVertex().getId()));
                // normal case
                e.setOppositeEdge(allHalfEdges.get(e.getNextEdge().getVertex().getId()).get(e.getVertex().getId()));
                if (allCells.get(e.getFaceId()) == null) {
                    allCells.put(e.getFaceId(), new TectonicCell(e.getFaceId(), e, this));
                }
            }
        }

    }

    public ConcurrentHashMap<BigDecimal, ConcurrentHashMap<BigDecimal, Vertex>> getAllVertices() {
        return allVertices;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, HalfEdge>> getAllHalfEdges() {
        return allHalfEdges;
    }

    public ConcurrentHashMap<Integer, TectonicCell> getAllCells() {
        return allCells;
    }

    // distribute the cells in the network to n tectonic plates
    public void distributeCells(int n) {
        for (int i = 0; i < n; i++) {
            plates.add(new TectonicPlate(i));
        }

        ArrayList<TectonicCell> remainingCells = new ArrayList<>(allCells.values());

        SecureRandom r = new SecureRandom();
        for (int i = 0; i < n; i++) {
            //System.out.println(remainingCells.size());
            TectonicCell c = remainingCells.remove(r.nextInt(remainingCells.size()));
            plates.get(i).addCell(c);
            allCells.get(c.getFaceId()).setParentId(i);
        }

        int currentPlateIndex = 0;
        while (!remainingCells.isEmpty()) {

            // add adjacent cell if possible
            System.out.println("id: " + plates.get(currentPlateIndex).getRandomAdjacentCellId());
            TectonicCell adjCell = allCells.get(plates.get(currentPlateIndex).getRandomAdjacentCellId());
            if (adjCell != null) {
                remainingCells.remove(adjCell);
                System.out.println("ADDING");
                adjCell.setParentId(currentPlateIndex);
                plates.get(currentPlateIndex).addCell(adjCell);
            }
            System.out.println("Remaining: " + remainingCells.size());

            currentPlateIndex++;
            if (currentPlateIndex >= n)
                currentPlateIndex = 0;
        }
    }

    public TectonicCell getCell(int id) {
        return allCells.get(id);
    }

    public ArrayList<TectonicPlate> getPlates() {
        return plates;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);

        boolean showPlates = true;

        /*for (ConcurrentHashMap<BigDecimal, Vertex> c : this.allVertices.values())
            for (Vertex v : c.values())
                g.fillRect(v.getRealX().intValue()  + 100, v.getRealY().intValue()  + 100 , 1, 1);

        for (ConcurrentHashMap<Integer, HalfEdge> hel : this.allHalfEdges.values())
            for (HalfEdge he : hel.values())
                g.drawLine(he.getVertex().getX().intValue() + 100, he.getVertex().getY().intValue() + 100, he.getNextEdge().getVertex().getX().intValue() + 100, he.getNextEdge().getVertex().getY().intValue() + 100);
        */
        if (showPlates) {
            // cell width and height is all equal
            for (TectonicPlate t : plates) {
                g.setColor(t.getColor());

                System.out.println(t.getCells().size());
                for (TectonicCell c : t.getCells()) {
                    BigDecimal[] x = c.getTopLeftCoordinate();
                    g.fillRect(x[0].intValue() + 100, x[1].intValue() + 100, cellW.intValue(), cellH.intValue());
                }
            }
        }
    }



    // Assigns TectonicPlate pointers to their proper Location objects
    public void assignParentPlates(Location[] locs) {
        int n = 0;
        for (Location l : locs) {
            TectonicPlate t = findPlateAt(l.getX(), l.getY());
            System.out.println("loc: " + n);
            l.setParentPlate(t);
            n++;
        }
    }

    // todo super naive but whatever
    private TectonicPlate findPlateAt(BigDecimal x, BigDecimal y) {

        // find the closest cell to the point, return its plate
        TectonicCell closestCell = null;
        BigDecimal closestDistanceSq = BigDecimal.valueOf(Double.MAX_VALUE);

        /*System.out.println(closestCell.getTopLeftCoordinate()[0]);
        System.out.println(closestCell.getTopLeftCoordinate()[1]);
        System.out.println(closestCell.getCenterCoordinate()[0]);
        System.out.println(closestCell.getCenterCoordinate()[1]);*/
        BigDecimal reqDistSq = BigDecimal.ZERO.subtract(cellW.divide(new BigDecimal(2))).pow(2).add(BigDecimal.ZERO.subtract(cellH.divide(new BigDecimal(2)))).pow(2);

        for (TectonicCell c : allCells.values()) {
            BigDecimal[] center = c.getCenterCoordinate();
            BigDecimal dSq = x.subtract(center[0]).pow(2).add(y.subtract(center[1]).pow(2));
            if (dSq.compareTo(closestDistanceSq) < 0) {
                closestDistanceSq = dSq;
                closestCell = c;
                if (closestDistanceSq.compareTo(reqDistSq) <= 0)
                    return plates.get(closestCell.getParentId());
            }
        }
        return plates.get(closestCell.getParentId());

    }

    public BigDecimal getCellW() {
        return cellW;
    }

    public BigDecimal getCellH() {
        return cellH;
    }
}

