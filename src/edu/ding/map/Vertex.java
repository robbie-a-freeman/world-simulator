package edu.ding.map;

import java.math.BigDecimal;

public class Vertex {

    private BigDecimal x;
    private BigDecimal y;
    private int id;
    private HalfEdge outgoingHalfEdge;
    // if this is a virtual vertex, refer to this one
    private Vertex refVertex;

    public Vertex(int id, BigDecimal[] x) {
        this.x = x[0];
        this.y = x[1];
        this.id = id;
        refVertex = null;
    }

    // for virtual vertices, AKA the ones at x = 500, and y = 0/y = 500
    public Vertex(int id, BigDecimal[] x, Vertex v) {
        this.x = x[0];
        this.y = x[1];
        this.id = id;
        refVertex = v;
    }

    @Override
    public String toString() {
        return "Vertex " + id + " at " + x + ", " + y;
    }

    public void setOutgoingHalfEdge(HalfEdge outgoingHalfEdge) {
        this.outgoingHalfEdge = outgoingHalfEdge;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getRealX() {
        if (refVertex != null)
            return refVertex.getRealX();
        return x;
    }

    public BigDecimal getRealY() {
        if (refVertex != null)
            return refVertex.getRealY();
        return y;
    }

    public BigDecimal[] getXY() {
        return new BigDecimal[]{x, y};
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public void safeTranslate(BigDecimal x, BigDecimal y) {
        if (refVertex != null) {
            this.x = this.x.add(x);
            this.y = this.y.add(y);

            if (this.y.compareTo(new BigDecimal(0)) < 0) {
                this.y = new BigDecimal(0).subtract(this.y); // tODO generalize
                this.x = this.x.add(new BigDecimal(250));
            } else if (this.y.compareTo(new BigDecimal(500)) > 0) {
                this.y = new BigDecimal(500).subtract(this.y.subtract(new BigDecimal(500)));
                this.x = this.x.add(new BigDecimal(250));
            }

            if (this.x.compareTo(new BigDecimal(0)) < 0) {
                this.x = new BigDecimal(500).add(this.x); // tODO generalize
            } else if (this.x.compareTo(new BigDecimal(500)) > 0) {
                this.x = new BigDecimal(0).add(this.x.subtract(new BigDecimal(500)));
            }
        }
    }

    public int getId() {
        return id;
    }

    public Vertex getRefVertex() {
        return refVertex;
    }
}
