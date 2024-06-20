package org.scratchGame.mavenScratchGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WonGame {
    private String[][] matrix;
    public String[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(String[][] matrix) {
		this.matrix = matrix;
	}
	private long reward;
    @JsonIgnore
    private Map<String, Object> appliedWinningCombinations = new HashMap<String, Object>();
    @JsonProperty("applied_bonus_symbol")
    public String[] appliedBonusSymbol;
	
	
	
	@Override
	public String toString() {
		return "WonGame [matrix=" + Arrays.toString(matrix) + ", reward=" + reward + ", appliedWinningCombinations="
				+ appliedWinningCombinations + ", appliedBonusSymbol=" + Arrays.toString(appliedBonusSymbol) + "]";
	}
	public String[] getAppliedBonusSymbol() {
		return appliedBonusSymbol;
	}
	public void setAppliedBonusSymbol(String[] appliedBonusSymbol) {
		this.appliedBonusSymbol = appliedBonusSymbol;
	}
	public long getReward() {
		return reward;
	}
	public void setReward(long reward) {
		this.reward = reward;
	}
	public Map<String, Object> getAppliedWinningCombinations() {
		return appliedWinningCombinations;
	}
	public void setAppliedWinningCombinations(Map<String, Object> appliedWinningCombinations) {
		this.appliedWinningCombinations = appliedWinningCombinations;
	}
	
    
}
