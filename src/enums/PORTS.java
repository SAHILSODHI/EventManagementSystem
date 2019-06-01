package enums;

public enum PORTS {
	TOR(2966),
	MTL(2964),
	OTW(2965);
	
	public final Integer label;
	 
    PORTS(Integer label) {
        this.label = label;
    }
 
}
