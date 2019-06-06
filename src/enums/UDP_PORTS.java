package enums;

public enum UDP_PORTS {
	TOR(2966),
	MTL(2964),
	OTW(2965);
	
	public final Integer label;
	 
    UDP_PORTS(Integer label) {
        this.label = label;
    }
 
}
