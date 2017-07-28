package community.graph;

public class Edge {

	private final int mID;
    private double mWeight;

    public Edge(int x)
    {
        mID = x;
    }

    public int getID()
    {
        return mID;
    }

    public void setWeight(double mWeight)
    {
        this.mWeight = mWeight;
    }

    public double getWeight()
    {
        return mWeight;
    }

    public String toString()
    {
        return (Double.toString(this.getWeight()));
    }
}
