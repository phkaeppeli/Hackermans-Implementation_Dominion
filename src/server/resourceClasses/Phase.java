package server.resourceClasses;

/**
 * 
 * @author Mario Allemann
 *
 */
public enum Phase {
	ACTION, BUY, CLEANUP, WAITING;

	/**
	 * Returns the next Phase based on the currentPhase
	 * 
	 * @param currentPhase
	 *            The current phase
	 * @return The next phase
	 * 
	 * @author Mario Allemann
	 */
	public Phase getNextPhase(Phase currentPhase) {
		Phase[] phase = Phase.values();

		// Loop back around
		if (currentPhase == (Phase.WAITING)) {
			return Phase.ACTION;
		}

		for (int count = 0; count < phase.length; count++) {
			if (phase[count].equals(this)) {
				// Return next phase
				return phase[count + 1];
			}
		}
		return null;
	}

}
