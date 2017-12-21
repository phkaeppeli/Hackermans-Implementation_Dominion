package client.resourceClasses;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class ListEntry {
	private String rank;
	private String name;
	private String parameter;
	
	public ListEntry(String n, String p){
		this.name = n;
		this.parameter = p;
	}
	
	public ListEntry(String r, String n, String p) {
		this.rank = r;
		this.name = n;
		this.parameter = p;
	}
	
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return this.parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
