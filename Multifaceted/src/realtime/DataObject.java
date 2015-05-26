package realtime;

public class DataObject implements Comparable<DataObject>{
	
	private String id;
	private String label;
	private int type;
	
	private double sortingScore;
	public DataObject(String id, String label, int type){
		this.id = id;
		this.label = label;
		this.type = type;
		
		this.setSortingScore(0);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{id:"+id+", label:"+label+", type:"+type+", sortingScore:"+sortingScore+"}";
	}
	
	public String getStringValue()
	{
		return ""+this.type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getSortingScore() {
		return sortingScore;
	}

	public void setSortingScore(double sortingScore) {
		this.sortingScore = sortingScore;
	}

	@Override
	public int compareTo(DataObject o) {
		// TODO Auto-generated method stub
		return Double.compare(o.getSortingScore(), this.getSortingScore() );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataObject other = (DataObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
