package edu.ncsu.csc.itrust;

import java.util.*;

import edu.ncsu.csc.itrust.CauseOfDeathInfo;

public class CauseOfDeathRankings {
	private List<CauseOfDeathInfo> codLHCP;
	private List<CauseOfDeathInfo> codAll;

	public CauseOfDeathRankings(List<CauseOfDeathInfo> LHCP, List<CauseOfDeathInfo> all) 
	{
		this.codLHCP = LHCP;
		this.codAll = all;
	}

	public void setCauseOfDeathLHCP(CauseOfDeathInfo cod, Integer rank) {
		this.codLHCP.set(rank, cod);
	}

	public void setCauseOfDeathAll(CauseOfDeathInfo cod, Integer rank) {
		this.codAll.set(rank, cod);
	}

	public CauseOfDeathInfo getCauseOfDeathLHCP(Integer rank) {
		CauseOfDeathInfo cod = null;
		if (rank < this.codLHCP.size()) {
			// prevents accessing an out-of-bounds index
			cod = this.codLHCP.get(rank);
		}
		return cod;
	}

	public CauseOfDeathInfo getCauseOfDeathAll(Integer rank) {
		CauseOfDeathInfo cod = null;
		if (rank < this.codAll.size()) {
			// prevents accessing an out-of-bounds index
			cod = this.codAll.get(rank);
		}
		return cod;
	}

	public List<CauseOfDeathInfo> getCauseOfDeathLHCP() {
		return this.codLHCP;
	}

	public List<CauseOfDeathInfo> getCauseOfDeathAll() {
		return this.codAll;
	}

	public void setCauseOfDeathLHCP(List<CauseOfDeathInfo> codLHCP) {
		this.codLHCP = codLHCP;
	}

	public void setCauseOfDeathAll(List<CauseOfDeathInfo> codAll) {
		this.codAll = codAll;
	}
}
