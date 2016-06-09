import org.apache.log4j.Logger;

public class AddressRange {
	
	private static final Logger logger = Logger.getLogger(AddressRange.class);

	private int ip;
	
	private int mask;
	
	private boolean active;
	
	/**
	 * Inicializa um intervalo de rede baseado na notacao CIDR (x.x.x.x/prefix).
	 * Se o parametro range for um IP sem prefix, assume prefix 32 (perfect match).
	 * @param range
	 * @param active
	 */
	public AddressRange(String range, boolean active) {
		String[] split = range.split("/");

		if (split.length == 2) {
			this.ip = ipToInt(split[0]);
			this.mask = cidrToNetmask(Integer.valueOf(split[1]));

		} else {
			this.ip = ipToInt(range);
			this.mask = cidrToNetmask(32);
		}
		
		this.active = active;
	}

	public AddressRange(String ip, int prefix, boolean active) {
		this.ip = ipToInt(ip);
		this.mask = cidrToNetmask(prefix);
		this.active = active;
	}

	public boolean isIPWithinRange(String ip) {
		try {
			return (this.active) && ((this.ip & this.mask) == (ipToInt(ip) & this.mask));
			
		} catch(NumberFormatException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception while converting IP", e);
			}
			return false;
		}
	}

	private static int ipToInt(String ip) {
		int value = 0;

		if (ip != null) {
			String[] octs = ip.split("\\.");
	
			if (octs.length == 4) {
				for (int i = 1; i < 5; i++) {
					int intValue = Integer.valueOf(octs[i - 1]);
					value += intValue << (4 - i) * 8;
				}
			}
		}

		return value;
	}

	private static int cidrToNetmask(int prefix) {
		return 0xFFFFFFFF << (32 - prefix);
	}

	public String toString() {
		return new StringBuffer().append(this.ip).append("/").append(this.mask).toString();
	}

	public static String printBits(int value) {
		StringBuffer sb = new StringBuffer();
		int mask = 1 << 31;

		for (int i = 1; i <= 32; i++) {
			sb.append((value & mask) == 0 ? "0" : "1");
			value <<= 1;
			if ((i % 8) == 0) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

}
