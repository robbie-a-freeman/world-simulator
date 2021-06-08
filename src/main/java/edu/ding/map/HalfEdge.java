package edu.ding.map;

public class HalfEdge {
    private Vertex vertex;
    private HalfEdge oppositeEdge;
    private int faceId; //private TectonicPlate face;
    private HalfEdge nextEdge;
    private int id;

    public HalfEdge(int id, Vertex v, HalfEdge o, int f, HalfEdge n) {
        vertex = v;
        oppositeEdge = o;
        faceId = f;
        nextEdge = n;
        this.id = id;
    }

    @Override
    public String toString() {
        if (vertex == null) {
            System.err.println("vertex is null at edge " + id);
        }
        if (nextEdge.getVertex() == null) {
            System.err.println("next is null at edge " + id);
        }
        return "HalfEdge " + id + " from " + vertex.toString() + " to " + nextEdge.getVertex().toString();
    }

    public void setNextEdge(HalfEdge nextEdge) {
        this.nextEdge = nextEdge;
    }

    public void setOppositeEdge(HalfEdge oppositeEdge) {
        this.oppositeEdge = oppositeEdge;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public HalfEdge getOppositeEdge() {
        return oppositeEdge;
    }

    public int getFaceId() {
        return faceId;
    }

    public HalfEdge getNextEdge() {
        return nextEdge;
    }

    public int getId() {
        return id;
    }
}